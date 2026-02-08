package com.payflow.service;

import com.payflow.entity.IdempotencyKey;
import com.payflow.entity.Transaction;
import com.payflow.entity.Wallet;
import com.payflow.enums.TransactionType;
import com.payflow.exception.GlobalExceptionHandler;
import com.payflow.exception.InsufficientBalanceException;
import com.payflow.repository.IdempotencyRepository;
import com.payflow.repository.TransactionRepository;
import com.payflow.repository.WalletRepository;
import com.sun.jdi.request.DuplicateRequestException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
public class WalletService{
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final IdempotencyRepository idempotencyRepository;


    public void credit(Long walletId, BigDecimal amount,String idempotenceKey)
    {
//        if (idempotencyRepository.findByIdempotencyKey(idempotenceKey).isPresent())
//        {
//            return;
//        }
        Wallet wallet=getWallet(walletId);
        BigDecimal newBalance=wallet.getBalance().add(amount);
        wallet.setBalance(newBalance);
        transactionRepository.save(new Transaction(wallet, TransactionType.CREDIT,amount,newBalance));
        idempotencyRepository.save(new IdempotencyKey(idempotenceKey,hash(walletId,amount)));
    }


    public void debit(Long walletId,BigDecimal amount,String idempotencyKey)
    {
//        if (idempotencyRepository.findByIdempotencyKey(idempotencyKey).isPresent())
//        {
//           return;
//        }
        Wallet wallet=getWallet(walletId);
        if(wallet.getBalance().compareTo(amount)<0)
        {
            throw  new InsufficientBalanceException("Insufficient Balance");
        }
       BigDecimal newBalance=wallet.getBalance().subtract(amount);
        wallet.setBalance(newBalance);
        transactionRepository.save(new Transaction(wallet,TransactionType.DEBIT,amount,newBalance));
        idempotencyRepository.save(new IdempotencyKey(idempotencyKey,hash(walletId,amount)));
    }

    private String hash(Long walletId, BigDecimal amount) {
        return walletId + ":" + amount;
    }
    public Wallet getWallet(Long walletId)
    {
        return walletRepository.findById(walletId).orElseThrow(()->new EntityNotFoundException("Wallet Not Found"));
    }

}
