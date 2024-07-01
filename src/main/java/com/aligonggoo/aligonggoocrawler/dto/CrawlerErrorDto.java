package com.aligonggoo.aligonggoocrawler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CrawlerErrorDto {

    public enum ErrorType {
        LOG,RESTART
    }

    private ErrorType errorType;
    private String message;
    private String url;
    private String stackTrace;
    private LocalDateTime time;
}
