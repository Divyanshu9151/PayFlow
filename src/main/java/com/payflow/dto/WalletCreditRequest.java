package com.payflow.dto;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class WalletCreditRequest {
    @Positive
    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
