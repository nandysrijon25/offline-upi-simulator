package com.srijon.offlineupi.controller;

import com.srijon.offlineupi.dto.PaymentRequest;
import com.srijon.offlineupi.dto.PaymentResponse;
import com.srijon.offlineupi.model.Account;
import com.srijon.offlineupi.model.MeshPacket;
import com.srijon.offlineupi.model.Payment;
import com.srijon.offlineupi.service.AccountService;
import com.srijon.offlineupi.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PaymentController {
    private final PaymentService paymentService;
    private final AccountService accountService;

    public PaymentController(PaymentService paymentService, AccountService accountService) {
        this.paymentService = paymentService;
        this.accountService = accountService;
    }

    @GetMapping("/accounts")
    public List<Account> accounts() {
        return accountService.getAll();
    }

    @GetMapping("/payments")
    public List<Payment> payments() {
        return paymentService.getAllPayments();
    }

    @PostMapping("/payments/offline")
    public PaymentResponse createOffline(@RequestBody PaymentRequest request) {
        return paymentService.createOfflinePayment(request);
    }

    @PostMapping("/payments/{transactionId}/sync")
    public PaymentResponse sync(@PathVariable String transactionId) {
        return paymentService.syncPayment(transactionId);
    }

    @GetMapping("/mesh/packets")
    public List<MeshPacket> packets() {
        return paymentService.getAllPackets();
    }

    @PostMapping("/mesh/gossip")
    public String gossip() {
        paymentService.runGossipRound();
        return "One mesh gossip round completed.";
    }


}
