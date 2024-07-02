package com.aligonggoo.aligonggoocrawler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CrawlerErrorDto {

    public enum ErrorType {
        RESTART, NOT_AVAILABLE, LOG
    }

    @Value("${instance.id}")
    private String instanceId;

    private ErrorType errorType;
    private String message;
    private String url;
    private String stackTrace;
    private final LocalDateTime time = LocalDateTime.now().withNano(0);

}
