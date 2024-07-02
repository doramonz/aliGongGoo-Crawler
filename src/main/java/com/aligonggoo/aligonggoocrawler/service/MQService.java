package com.aligonggoo.aligonggoocrawler.service;

import com.aligonggoo.aligonggoocrawler.dto.AliProductInfo;
import com.aligonggoo.aligonggoocrawler.dto.CrawlerErrorDto;
import com.aligonggoo.aligonggoocrawler.dto.URLParsingDto;
import com.aligonggoo.aligonggoocrawler.exception.HTTPGETRequestFailException;
import com.aligonggoo.aligonggoocrawler.exception.NotAvailableException;
import com.aligonggoo.aligonggoocrawler.exception.RestartException;
import com.aligonggoo.aligonggoocrawler.mq.OutputRabbitMQ;
import com.aligonggoo.aligonggoocrawler.util.AliProductUtil;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MQService {

    private final OutputRabbitMQ outputRabbitMQ;
    private final AliProductUtil aliProductUtil;
    private final ApplicationContext applicationContext;

    public void processURL(URLParsingDto urlParsingDto) throws RestartException {
        AliProductInfo aliProductInfo;
        try {
            aliProductInfo = aliProductUtil.getProductInfo(urlParsingDto.getUrl());
            outputRabbitMQ.sendProductInfo(aliProductInfo);
        } catch (Exception e) {
            CrawlerErrorDto errorDto;
            if (e instanceof HTTPGETRequestFailException) {
                log.error("HTTP GET Request Fail");
                errorDto = CrawlerErrorDto.builder()
                        .url(urlParsingDto.getUrl())
                        .message("HTTP GET Request Fail")
                        .stackTrace(Arrays.toString(e.getStackTrace()))
                        .errorType(CrawlerErrorDto.ErrorType.LOG)
                        .build();
            } else if (e instanceof NotAvailableException) {
                log.error("Not Available Exception");
                errorDto = CrawlerErrorDto.builder()
                        .url(urlParsingDto.getUrl())
                        .message("Not Available Exception")
                        .stackTrace(Arrays.toString(e.getStackTrace()))
                        .errorType(CrawlerErrorDto.ErrorType.NOT_AVAILABLE)
                        .build();
            } else if (e instanceof RestartException) {
                log.error("Restart Exception");
                stopQueue("inputQueue");
                errorDto = CrawlerErrorDto.builder()
                        .url(urlParsingDto.getUrl())
                        .message("Restart Exception")
                        .stackTrace(Arrays.toString(e.getStackTrace()))
                        .errorType(CrawlerErrorDto.ErrorType.RESTART)
                        .build();
                outputRabbitMQ.sendCrawlerError(errorDto);
                throw new RestartException();
            } else {
                log.error("Unknown Exception");
                errorDto = CrawlerErrorDto.builder().build();
            }
            outputRabbitMQ.sendCrawlerError(errorDto);
        }
    }

    void stopQueue(String queueName) {
        log.info("Stopping queue: {}", queueName);
        RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry = applicationContext.getBean(RabbitListenerEndpointRegistry.class);
        MessageListenerContainer listener = rabbitListenerEndpointRegistry.getListenerContainer(queueName);
        listener.stop();
    }
}
