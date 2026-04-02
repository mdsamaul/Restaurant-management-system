package com.rms.service;
import com.rms.dto.request.PaymentRequest;
import com.rms.dto.response.PaymentResponse;
public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest req);
    PaymentResponse getPaymentByOrderId(Long orderId);
}
