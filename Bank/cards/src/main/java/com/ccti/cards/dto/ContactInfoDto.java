package com.ccti.cards.dto;

import java.util.*;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "contact")
public class ContactInfoDto {

    private String message;
    private Map<String, String> details;
    private List<String> phoneNumbers;

}