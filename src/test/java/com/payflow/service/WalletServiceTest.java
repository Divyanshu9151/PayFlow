package com.payflow.service;


import com.payflow.entity.Transaction;
import com.payflow.entity.User;
import com.payflow.entity.Wallet;
import com.payflow.exception.InsufficientBalanceException;
import com.payflow.repository.IdempotencyRepository;
import com.payflow.repository.TransactionRepository;
import com.payflow.repository.UserRepository;
import com.payflow.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private IdempotencyRepository idempotencyRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private WalletService walletService;

    @Test
    void shouldReturnWalletBalance() {
        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(100));

        when(walletRepository.findBalanceById(1L))
                .thenReturn(Optional.of(BigDecimal.valueOf(100)));

        BigDecimal balance = walletService.getWalletBalance(1L);

        assertEquals(BigDecimal.valueOf(100), balance);
    }
    @Test
    void shouldThrowExceptionWhenWalletNotFound() {

        when(walletRepository.findBalanceById(1L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                walletService.getWalletBalance(1L));
    }
    @Test
    void shouldCreditWalletSuccessfully() {

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(100));

        User user = new User();
        user.setEmail("test@example.com");

        when(walletRepository.findById(1L))
                .thenReturn(Optional.of(wallet));

        when(userRepository.findByWalletId(1L))
                .thenReturn(Optional.of(user));

        walletService.credit(1L, BigDecimal.valueOf(50), "key1");

        // ✅ Verify balance updated
        assertEquals(BigDecimal.valueOf(150), wallet.getBalance());

        // ✅ Verify transaction saved
        verify(transactionRepository).save(any(Transaction.class));

        // ✅ Verify idempotency saved
        verify(idempotencyRepository).save(any());

        // ✅ Verify email sent
        verify(notificationService).sendTransactionEmail("test@example.com");
    }
    @Test
    void shouldDebitWalletSuccessfully() {

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(200));

        when(walletRepository.findById(1L))
                .thenReturn(Optional.of(wallet));

        walletService.debit(1L, BigDecimal.valueOf(50), "key2");

        assertEquals(BigDecimal.valueOf(150), wallet.getBalance());

        verify(transactionRepository).save(any(Transaction.class));
        verify(idempotencyRepository).save(any());
    }
    @Test
    void shouldThrowExceptionWhenInsufficientBalance() {

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setBalance(BigDecimal.valueOf(50));

        when(walletRepository.findById(1L))
                .thenReturn(Optional.of(wallet));

        assertThrows(InsufficientBalanceException.class, () ->
                walletService.debit(1L, BigDecimal.valueOf(100), "key3"));
    }
}