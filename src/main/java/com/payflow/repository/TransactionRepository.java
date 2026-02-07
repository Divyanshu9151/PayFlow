package com.payflow.repository;

import com.payflow.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    List<Transaction>findByWalletIdOrderByCreatedAtDesc(Long walletId);
}
