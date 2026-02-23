package com.payflow.repository;

import com.payflow.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet,Long> {
    @Query("SELECT w.balance FROM Wallet w where w.id=:id")
    Optional<BigDecimal>findBalanceById(@Param("id")Long walletId);
}
