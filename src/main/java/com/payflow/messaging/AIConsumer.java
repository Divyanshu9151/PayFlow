package com.payflow.messaging;

import com.payflow.entity.Transaction;
import com.payflow.enums.Category;
import com.payflow.repository.TransactionRepository;
import com.payflow.service.AIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AIConsumer {

    private final AIService aiService;
    private final TransactionRepository transactionRepository;

    @RabbitListener(queues = "transaction.queue")
    public void consume(Map<String, Object> message) {

        Long transactionId = Long.valueOf(message.get("transactionId").toString());
        String description = message.get("description").toString();
        log.info("AI processing started for txnId={}", transactionId);
        try {
            Category category = aiService.categorize(description);
            Transaction txn = transactionRepository.findById(transactionId)
                    .orElseThrow();
            txn.setCategory(category);
            transactionRepository.save(txn);

            log.info("AI category updated = {} for txnId={}", category, transactionId);

        } catch (Exception e) {
            log.error("AI failed, fallback to OTHER for txnId={}", transactionId);

            Transaction txn = transactionRepository.findById(transactionId)
                    .orElseThrow();
            txn.setCategory(Category.OTHER);
            transactionRepository.save(txn);
        }
    }
}