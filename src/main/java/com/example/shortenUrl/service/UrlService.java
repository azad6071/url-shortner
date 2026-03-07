package com.example.shortenUrl.service;

import com.example.shortenUrl.entity.UrlMapping;
import com.example.shortenUrl.repository.UrlMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UrlService {

    @Autowired
    private UrlMappingRepository repository;

    private static final String BASE_URL = "http://localhost:8080/";

    public String shortenUrl(String originalUrl) {
        String shortCode = generateShortCode();
        UrlMapping mapping = new UrlMapping();
//        mapping.setOriginalUrl(originalUrl);
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
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private String normalizeUrl(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be empty");
        }
        // Add protocol if missing
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return "https://" + url;
        }
        return url;
    }
}
