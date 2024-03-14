package com.ccti.accounts.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "contact")
public record ContactInfoDto (String message, Map<String, String> details, List<String> phoneNumbers ){
}