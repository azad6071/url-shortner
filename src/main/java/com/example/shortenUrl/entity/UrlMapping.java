package com.example.shortenUrl.entity;


import jakarta.persistence.*;
import java.util.Date;

import lombok.Data;

@Entity
@Data
public class UrlMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_code")
    private String shortCode;

    @Column(name = "original_url")
    private String originalUrl;

    @Column(name = "create_date")
    private Date createDate = new Date();

}
