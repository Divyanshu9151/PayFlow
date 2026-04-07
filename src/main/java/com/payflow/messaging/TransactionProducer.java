package com.payflow.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionProducer {

    private final RabbitTemplate rabbitTemplate;

    private static final String QUEUE_NAME = "transaction.queue";

    public void sendTransaction(Object message) {
        log.info("Sending message to RabbitMQ | message={}", message);
        rabbitTemplate.convertAndSend(QUEUE_NAME, message);
        log.info("Message sent successfully ✅");
    }
}