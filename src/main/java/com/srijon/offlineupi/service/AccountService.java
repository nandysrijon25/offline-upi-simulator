package com.srijon.offlineupi.service;

import com.srijon.offlineupi.model.Account;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AccountService {
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    public AccountService() {
        accounts.put("srijon@upi", new Account("srijon@upi", "Srijon", new BigDecimal("5000.00")));
        accounts.put("rahul@upi", new Account("rahul@upi", "Rahul", new BigDecimal("1500.00")));
        accounts.put("demo@upi", new Account("demo@upi", "Demo User", new BigDecimal("3000.00")));
    }

    public List<Account> getAll() {
        return new ArrayList<>(accounts.values());
    }

    public Account get(String upiId) {
        return accounts.get(upiId);
    }

    public boolean hasEnoughBalance(String upiId, BigDecimal amount) {
        Account account = get(upiId);
        return account != null && account.getBalance().compareTo(amount) >= 0;
    }

    public void transfer(String senderUpiId, String receiverUpiId, BigDecimal amount) {
        Account sender = get(senderUpiId);
        Account receiver = get(receiverUpiId);
        if (sender == null || receiver == null) {
            throw new IllegalArgumentException("Sender or receiver account does not exist.");
        }
        if (!hasEnoughBalance(senderUpiId, amount)) {
            throw new IllegalArgumentException("Insufficient balance.");
        }
        sender.debit(amount);
        receiver.credit(amount);
    }


}
