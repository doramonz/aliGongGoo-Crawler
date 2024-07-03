package com.aligonggoo.aligonggoocrawler.mq;

import com.aligonggoo.aligonggoocrawler.dto.AliProductInfo;
import com.aligonggoo.aligonggoocrawler.dto.CrawlerErrorDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutputRabbitMQ implements OutputQueue {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.outputQueue.name}")
    private String queueName;

    @Value("${rabbitmq.errorQueue.name}")
    private String errorQueueName;

    public void sendProductInfo(AliProductInfo aliProductInfo) {
        rabbitTemplate.convertAndSend(queueName, aliProductInfo);
    }

    public void sendCrawlerError(CrawlerErrorDto crawlerErrorDto) {
        rabbitTemplate.convertAndSend(errorQueueName, crawlerErrorDto);
    }
}
