package com.payflow.messaging;

import com.payflow.entity.Transaction;
import com.payflow.enums.Category;
import com.payflow.repository.TransactionRepository;
import com.payflow.service.AIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AIConsumer {

    private final AIService aiService;
    private final TransactionRepository transactionRepository;

    @RabbitListener(
            queues = "transaction.queue",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void consume(TransactionMessage message) {

        Long transactionId = message.getTransactionId();
        String description = message.getDescription();

        log.info("AI processing started for txnId={}", transactionId);

        Transaction txn = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        // ✅ Idempotency check
        if (txn.getCategory() != null) {
            log.info("Already processed txnId={}, skipping", transactionId);
            return;
        }

        try {

            //just for testing DLQ
//            if (description.contains("FAIL")) {
//                throw new RuntimeException("Forced failure for testing");
//            }
            Category category = aiService.categorize(description);
            txn.setCategory(category);
            transactionRepository.save(txn);

            log.info("AI category updated = {} for txnId={}", category, transactionId);

        } catch (Exception e) {
            log.error("AI failed for txnId={}, retrying...", transactionId);
            throw new RuntimeException(e); // 🔥 triggers retry → DLQ
        }
    }
}