package com.srijon.offlineupi.dto;

import java.math.BigDecimal;

public record PaymentResponse(
        String transactionId,
        String senderUpiId,
        String receiverUpiId,
        BigDecimal amount,
        String status,
        String message
) {}
