package com.payflow.service;

import com.payflow.dto.WalletResponse;
import com.payflow.entity.IdempotencyKey;
import com.payflow.entity.Transaction;
import com.payflow.entity.User;
import com.payflow.entity.Wallet;
import com.payflow.enums.TransactionType;
import com.payflow.exception.InsufficientBalanceException;
import com.payflow.messaging.TransactionMessage;
import com.payflow.messaging.TransactionProducer;
import com.payflow.repository.IdempotencyRepository;
import com.payflow.repository.TransactionRepository;
import com.payflow.repository.UserRepository;
import com.payflow.repository.WalletRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final IdempotencyRepository idempotencyRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final TransactionProducer transactionProducer;

    // ✅ CREDIT
    @CacheEvict(value = "walletBalance", key = "#walletId")
    public void credit(Long walletId, BigDecimal amount, String idempotenceKey, String description) {

        log.info("Credit request received | walletId={} | amount={}", walletId, amount);

        Wallet wallet = getWallet(walletId);

        BigDecimal newBalance = wallet.getBalance().add(amount);
        wallet.setBalance(newBalance);

        // ✅ Save transaction FIRST
        Transaction txn = transactionRepository.save(
                new Transaction(wallet, TransactionType.CREDIT, amount, newBalance, null, description)
        );

        // ✅ Send message using DTO
        transactionProducer.sendTransaction(
                new TransactionMessage(txn.getId(), description)
        );

        // ✅ Idempotency
        idempotencyRepository.save(new IdempotencyKey(idempotenceKey, hash(walletId, amount)));

        // ✅ Notification
        User user = userRepository.findByWalletId(walletId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        notificationService.sendTransactionEmail(user.getEmail());

        log.info("Credit successful | walletId={} | newBalance={}", walletId, newBalance);
    }

    // ✅ DEBIT
    @CacheEvict(value = "walletBalance", key = "#walletId")
    public void debit(Long walletId, BigDecimal amount, String idempotencyKey, String description) {

        log.info("Debit request received | walletId={} | amount={}", walletId, amount);

        Wallet wallet = getWallet(walletId);

        if (wallet.getBalance().compareTo(amount) < 0) {
            log.warn("Debit failed - insufficient balance | walletId={} | balance={} | attempted={}",
                    walletId, wallet.getBalance(), amount);
            throw new InsufficientBalanceException("Not enough balance");
        }

        BigDecimal newBalance = wallet.getBalance().subtract(amount);
        wallet.setBalance(newBalance);

        // ✅ Save transaction FIRST
        Transaction txn = transactionRepository.save(
                new Transaction(wallet, TransactionType.DEBIT, amount, newBalance, null, description)
        );

        // ✅ Send message using DTO
        transactionProducer.sendTransaction(
                new TransactionMessage(txn.getId(), description)
        );

        // ✅ Idempotency
        idempotencyRepository.save(new IdempotencyKey(idempotencyKey, hash(walletId, amount)));

        // ✅ Optional: Notification consistency
        User user = userRepository.findByWalletId(walletId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        notificationService.sendTransactionEmail(user.getEmail());

        log.info("Debit successful | walletId={} | newBalance={}", walletId, newBalance);
    }

    private String hash(Long walletId, BigDecimal amount) {
        return walletId + ":" + amount;
    }

    public List<WalletResponse> getAllWallet() {
        return walletRepository.findAll()
                .stream()
                .map(wallet -> new WalletResponse(wallet.getId(), wallet.getBalance()))
                .toList();
    }

    public Wallet getWallet(Long walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet Not Found"));
    }

    @Cacheable(value = "walletBalance", key = "#walletId")
    public BigDecimal getWalletBalance(Long walletId) {
        log.info("Fetching balance from DB for walletId={}", walletId);
        return walletRepository.findBalanceById(walletId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet Not Found"));
    }
}