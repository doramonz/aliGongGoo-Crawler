package com.aligonggoo.aligonggoocrawler.mq;

import com.aligonggoo.aligonggoocrawler.dto.URLParsingDto;
import com.aligonggoo.aligonggoocrawler.service.MQService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InputRabbitMQ implements InputQueue {

    private final MQService mqService;

    @RabbitListener(queues = "${rabbitmq.inputQueue.name}", id = "inputQueue")
    public void receiveMessage(URLParsingDto urlParsingDto) {
        mqService.processURL(urlParsingDto);
    }
}
