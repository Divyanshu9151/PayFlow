package com.payflow.dto;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class WalletDebitRequest {
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Positive
    private BigDecimal amount;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
