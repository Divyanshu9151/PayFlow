package com.payflow.service;

import com.payflow.entity.Wallet;
import com.payflow.exception.InsufficientBalanceException;
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


    public void credit(Long walletId, BigDecimal amount)
    {
        Wallet wallet=getWallet(walletId);
        wallet.setBalance(wallet.getBalance().add(amount));
    }

    public void debit(Long walletId,BigDecimal amount)
    {
        Wallet wallet=getWallet(walletId);
        if(wallet.getBalance().compareTo(amount)<0)
        {
            throw  new InsufficientBalanceException("Insufficient Balance");
        }
        else
        {
            wallet.setBalance(wallet.getBalance().subtract(amount));
        }
    }
    public Wallet getWallet(Long walletId)
    {
        return walletRepository.findById(walletId).orElseThrow(()->new EntityNotFoundException("Wallet Not Found"));
    }

}
