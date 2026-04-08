package com.payflow.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 🔹 MAIN EXCHANGE
    @Bean
    public DirectExchange transactionExchange() {
        return new DirectExchange("transaction.exchange");
    }

    // 🔹 MAIN QUEUE + DLQ CONFIG
    @Bean
    public Queue transactionQueue() {
        return QueueBuilder.durable("transaction.queue")
                .withArgument("x-dead-letter-exchange", "dlx.exchange")
                .withArgument("x-dead-letter-routing-key", "dlq.routing")
                .build();
    }

    // 🔹 BIND MAIN QUEUE
    @Bean
    public Binding transactionBinding() {
        return BindingBuilder.bind(transactionQueue())
                .to(transactionExchange())
                .with("transaction.routing");
    }

    // 🔹 DLQ
    @Bean
    public Queue deadLetterQueue() {
        return new Queue("transaction.dlq", true);
    }

    // 🔹 DLX
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("dlx.exchange");
    }

    // 🔹 DLQ BINDING
    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with("dlq.routing");
    }

    // 🔹 JSON CONVERTER
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // 🔹 RETRY CONFIG
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);

        // 🔥 VERY IMPORTANT
        factory.setMessageConverter(converter);

        factory.setAdviceChain(
                RetryInterceptorBuilder.stateless()
                        .maxAttempts(3)
                        .backOffOptions(2000, 2.0, 10000)
                        .recoverer(new RejectAndDontRequeueRecoverer())
                        .build()
        );

        return factory;
    }
}