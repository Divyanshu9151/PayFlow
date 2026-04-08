package com.payflow.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendTransaction(TransactionMessage message) {
        log.info("Sending message to RabbitMQ | message={}", message);

        rabbitTemplate.convertAndSend(
                "transaction.exchange",
                "transaction.routing",
                message
        );

        log.info("Message sent successfully ✅");
    }
}