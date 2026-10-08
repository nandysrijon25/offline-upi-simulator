package com.srijon.offlineupi.model;

import java.math.BigDecimal;

public class Account {
    private String upiId;
    private String name;
    private BigDecimal balance;

    public Account(String upiId, String name, BigDecimal balance) {
        this.upiId = upiId;
        this.name = name;
        this.balance = balance;
    }

    public String getUpiId() { return upiId; }
    public String getName() { return name; }
    public BigDecimal getBalance() { return balance; }

    public void debit(BigDecimal amount) {
        balance = balance.subtract(amount);
    }

    public void credit(BigDecimal amount) {
        balance = balance.add(amount);
    }
}
