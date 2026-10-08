package com.srijon.offlineupi.service;

import com.srijon.offlineupi.dto.PaymentRequest;
import com.srijon.offlineupi.dto.PaymentResponse;
import com.srijon.offlineupi.model.MeshPacket;
import com.srijon.offlineupi.model.Payment;
import com.srijon.offlineupi.model.PaymentStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PaymentService {
    private static final int INITIAL_TTL = 5;

    private final AccountService accountService;
    private final Map<String, Payment> payments = new ConcurrentHashMap<>();
    private final Map<String, MeshPacket> packets = new ConcurrentHashMap<>();
    private final Map<String, Boolean> settledTransactions = new ConcurrentHashMap<>();

    public PaymentService(AccountService accountService) {
        this.accountService = accountService;
    }

    public PaymentResponse createOfflinePayment(PaymentRequest request) {
        validateRequest(request);

        if (accountService.get(request.getSenderUpiId()) == null ||
                accountService.get(request.getReceiverUpiId()) == null) {
            throw new IllegalArgumentException("Sender or receiver account does not exist.");
        }

        String transactionId = "TXN-" + shortId();
        Payment payment = new Payment(
                transactionId,
                request.getSenderUpiId(),
                request.getReceiverUpiId(),
                request.getAmount(),
                PaymentStatus.QUEUED_OFFLINE,
                request.getNote()
        );

        payments.put(transactionId, payment);
        packets.put(transactionId, new MeshPacket("PKT-" + shortId(), transactionId, INITIAL_TTL));

        return toResponse(payment, "Payment queued for offline synchronization.");
    }

    public List<Payment> getAllPayments() {
        return new ArrayList<>(payments.values());
    }

    public List<MeshPacket> getAllPackets() {
        return new ArrayList<>(packets.values());
    }

    public PaymentResponse syncPayment(String transactionId) {
        Payment payment = payments.get(transactionId);
        if (payment == null) {
            throw new IllegalArgumentException("Transaction not found.");
        }

        if (settledTransactions.putIfAbsent(transactionId, Boolean.TRUE) != null) {
            payment.setStatus(PaymentStatus.DUPLICATE);
            return toResponse(payment, "This transaction was already processed.");
        }

        try {
            accountService.transfer(
                    payment.getSenderUpiId(),
                    payment.getReceiverUpiId(),
                    payment.getAmount()
            );
            payment.setStatus(PaymentStatus.SETTLED);
            return toResponse(payment, "Payment successfully settled.");
        } catch (RuntimeException ex) {
            settledTransactions.remove(transactionId);
            payment.setStatus(PaymentStatus.FAILED);
            return toResponse(payment, ex.getMessage());
        }
    }

    public void runGossipRound() {
        packets.values().forEach(MeshPacket::forward);
    }

    public void reset() {
        payments.clear();
        packets.clear();
        settledTransactions.clear();
    }

    private void validateRequest(PaymentRequest request) {
        if (request.getSenderUpiId() == null || request.getReceiverUpiId() == null) {
            throw new IllegalArgumentException("Sender and receiver UPI IDs are required.");
        }
        if (request.getSenderUpiId().equals(request.getReceiverUpiId())) {
            throw new IllegalArgumentException("Sender and receiver must be different.");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
    }

    private PaymentResponse toResponse(Payment p, String message) {
        return new PaymentResponse(
                p.getTransactionId(), p.getSenderUpiId(), p.getReceiverUpiId(),
                p.getAmount(), p.getStatus().name(), message
        );
    }

    private String shortId() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
