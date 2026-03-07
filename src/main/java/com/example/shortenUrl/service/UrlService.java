package com.example.shortenUrl.service;

import com.example.shortenUrl.entity.UrlMapping;
import com.example.shortenUrl.repository.UrlMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import java.util.Random;

@Service
public class UrlService {

    private static final String BASE_URL = "http://localhost:8081/";
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int SHORT_CODE_LENGTH = 7;
    private static final Random RANDOM = new Random();

    @Autowired
    private UrlMappingRepository repository;

    public String shortenUrl(String originalUrl) {
        String shortCode;
        do {
            shortCode = generateShortCode();
        } while (repository.existsByShortCode(shortCode));

        UrlMapping mapping = new UrlMapping();
        mapping.setOriginalUrl(normalizeUrl(originalUrl));
        mapping.setShortCode(shortCode);

        repository.save(mapping);
        return BASE_URL + shortCode;
    }

    public String getOriginalUrl(String shortCode) {
        return repository.findByShortCode(shortCode)
                .map(UrlMapping::getOriginalUrl)
                .orElseThrow(() -> new RuntimeException("Short URL not found"));
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
