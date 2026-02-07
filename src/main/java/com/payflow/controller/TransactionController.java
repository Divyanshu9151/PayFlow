package com.payflow.controller;

import com.payflow.dto.TransactionResponse;
import com.payflow.enums.TransactionType;
import com.payflow.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionRepository transactionRepository;

    @GetMapping("/{walletId}/transactions")
    public List<TransactionResponse> getTransactions(@PathVariable Long walletId)
    {
        return transactionRepository.findByWalletIdOrderByCreatedAtDesc(walletId)
                .stream()
                .map(TransactionResponse::new)
                .toList();
    }
}