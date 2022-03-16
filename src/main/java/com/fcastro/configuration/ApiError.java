package com.fcastro.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter
/**
 * Based on RFC https://tools.ietf.org/html/rfc7807
 */
public class ApiError {
    private final String type;
    private final String title;
    private final int status;
    private final String instance;
    private final Long timestamp;
    private final String path;
}

