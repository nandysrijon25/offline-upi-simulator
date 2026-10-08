package com.srijon.offlineupi;

import com.srijon.offlineupi.dto.PaymentRequest;
import com.srijon.offlineupi.dto.PaymentResponse;
import com.srijon.offlineupi.service.AccountService;
import com.srijon.offlineupi.service.PaymentService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentServiceTest {

    @Test
    void paymentSettlesOnlyOnce() {
        AccountService accounts = new AccountService();
        PaymentService payments = new PaymentService(accounts);

        PaymentRequest request = new PaymentRequest();
        request.setSenderUpiId("srijon@upi");
        request.setReceiverUpiId("rahul@upi");
        request.setAmount(new BigDecimal("500"));

        PaymentResponse created = payments.createOfflinePayment(request);
        PaymentResponse first = payments.syncPayment(created.transactionId());
        PaymentResponse second = payments.syncPayment(created.transactionId());

        assertEquals("SETTLED", first.status());
        assertEquals("DUPLICATE", second.status());
        assertEquals(new BigDecimal("4500.00"), accounts.get("srijon@upi").getBalance());
        assertEquals(new BigDecimal("2000.00"), accounts.get("rahul@upi").getBalance());
    }
}
