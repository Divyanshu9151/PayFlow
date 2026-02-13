package com.payflow.service;


import com.payflow.dto.TransactionResponse;
import com.payflow.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public Page<TransactionResponse> getWalletTransactions(Long walletId,int page,int size,String sortField)
    {
        Pageable pageable= PageRequest.of(page,size, Sort.by(sortField).ascending());
        return transactionRepository.findByWalletId(walletId,pageable).map(TransactionResponse::new);

    }
}
