package com.srijon.offlineupi.model;

import java.math.BigDecimal;
import java.time.Instant;

public class Payment {
    private final String transactionId;
    private final String senderUpiId;
    private final String receiverUpiId;
    private final BigDecimal amount;
    private final Instant createdAt;
    private PaymentStatus status;
    private final String note;

    public Payment(String transactionId, String senderUpiId, String receiverUpiId,
                   BigDecimal amount, PaymentStatus status, String note) {
        this.transactionId = transactionId;
        this.senderUpiId = senderUpiId;
        this.receiverUpiId = receiverUpiId;
        this.amount = amount;
        this.createdAt = Instant.now();
        this.status = status;
        this.note = note == null ? "" : note;
    }

    public String getTransactionId() { return transactionId; }
    public String getSenderUpiId() { return senderUpiId; }
    public String getReceiverUpiId() { return receiverUpiId; }
    public BigDecimal getAmount() { return amount; }
    public Instant getCreatedAt() { return createdAt; }
    public PaymentStatus getStatus() { return status; }
    public String getNote() { return note; }
    public void setStatus(PaymentStatus status) { this.status = status; }
}
