package com.payflow.dto;

import java.math.BigDecimal;

public class WalletResponse {


    private Long id;
    private BigDecimal balance;

    public WalletResponse(Long id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

}
