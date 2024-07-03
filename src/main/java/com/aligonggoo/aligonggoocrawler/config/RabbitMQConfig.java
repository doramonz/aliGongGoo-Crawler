package com.aligonggoo.aligonggoocrawler.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    @Value("${rabbitmq.inputQueue.name}")
    private String inputQueueName;

    @Value("${rabbitmq.outputQueue.name}")
    private String outputQueueName;

    @Value("${rabbitmq.errorQueue.name}")
    private String errorQueueName;

    @Bean
    public Queue inputQueue() {
        return new Queue(inputQueueName);
    }

    @Bean
    public Queue outputQueue() {
        return new Queue(outputQueueName);
    }

    @Bean
    public Queue errorQueue() {
        return new Queue(errorQueueName);
    }

    @Bean
    public Exchange inputExchange() {
        return new DirectExchange(inputQueueName);
    }

    @Bean
    public Exchange outputExchange() {
        return new DirectExchange(outputQueueName);
    }

    @Bean
    public Exchange errorExchange() {
        return new DirectExchange(errorQueueName);
    }

    @Bean
    public Binding inputBinding() {
        return BindingBuilder.bind(inputQueue()).to(inputExchange()).with(inputQueueName).noargs();
    }

    @Bean
    public Binding outputBinding() {
        return BindingBuilder.bind(outputQueue()).to(outputExchange()).with(outputQueueName).noargs();
    }

    @Bean
    public Binding errorBinding() {
        return BindingBuilder.bind(errorQueue()).to(errorExchange()).with(errorQueueName).noargs();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
