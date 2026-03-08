package com.example.shortenUrl.service;

import com.example.shortenUrl.entity.UrlMapping;
import com.example.shortenUrl.repository.UrlMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UrlService {

    @Value("${app.base-url}")
    private String baseUrl;

    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int SHORT_CODE_LENGTH = 7;
    private static final Random RANDOM = new Random();
    private static final String REDIS_KEY_PREFIX = "url:";
    private static final long REDIS_TTL_HOURS = 24;

    @Autowired
    private UrlMappingRepository repository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public String shortenUrl(String originalUrl) {
        String shortCode;
        do {
            shortCode = generateShortCode();
        } while (repository.existsByShortCode(shortCode));

        String normalizedUrl = normalizeUrl(originalUrl);

        UrlMapping mapping = new UrlMapping();
        mapping.setOriginalUrl(normalizedUrl);
        mapping.setShortCode(shortCode);

        repository.save(mapping);

        redisTemplate.opsForValue().set(REDIS_KEY_PREFIX + shortCode, normalizedUrl, REDIS_TTL_HOURS, TimeUnit.HOURS);

        return baseUrl + "/" + shortCode;
    }

    public String getOriginalUrl(String shortCode) {
        String cachedUrl = redisTemplate.opsForValue().get(REDIS_KEY_PREFIX + shortCode);
        if (cachedUrl != null) {
            return cachedUrl;
        }

        String originalUrl = repository.findByShortCode(shortCode)
                .map(UrlMapping::getOriginalUrl)
                .orElseThrow(() -> new RuntimeException("Short URL not found"));

        redisTemplate.opsForValue().set(REDIS_KEY_PREFIX + shortCode, originalUrl, REDIS_TTL_HOURS, TimeUnit.HOURS);

        return originalUrl;
    }

    private String generateShortCode() {
        StringBuilder sb = new StringBuilder(SHORT_CODE_LENGTH);
        long maxVal = (long) Math.pow(62, SHORT_CODE_LENGTH);
        long value = (long) (RANDOM.nextDouble() * maxVal);

        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            sb.append(BASE62_CHARS.charAt((int) (value % 62)));
            value /= 62;
        }

        return sb.toString();
    }

    private String normalizeUrl(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be empty");
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return "https://" + url;
        }
        return url;
    }
}
