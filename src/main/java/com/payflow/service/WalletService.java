package com.payflow.service;

import com.payflow.entity.Transaction;
import com.payflow.entity.Wallet;
import com.payflow.enums.TransactionType;
import com.payflow.exception.InsufficientBalanceException;
import com.payflow.repository.TransactionRepository;
import com.payflow.repository.WalletRepository;
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


    public void credit(Long walletId, BigDecimal amount)
    {
        Wallet wallet=getWallet(walletId);
        BigDecimal newBalance=wallet.getBalance().add(amount);
        wallet.setBalance(newBalance);
        transactionRepository.save(new Transaction(wallet, TransactionType.CREDIT,amount,newBalance));
    }


    public void debit(Long walletId,BigDecimal amount)
    {
        Wallet wallet=getWallet(walletId);
        if(wallet.getBalance().compareTo(amount)<0)
        {
            throw  new InsufficientBalanceException("Insufficient Balance");
        }
       BigDecimal newBalance=wallet.getBalance().subtract(amount);
        wallet.setBalance(newBalance);
        transactionRepository.save(new Transaction(wallet,TransactionType.DEBIT,amount,newBalance));
    }
    public Wallet getWallet(Long walletId)
    {
        return walletRepository.findById(walletId).orElseThrow(()->new EntityNotFoundException("Wallet Not Found"));
    }

}
