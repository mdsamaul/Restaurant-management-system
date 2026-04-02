package com.rms.controller;
import com.rms.dto.request.PaymentRequest;
import com.rms.dto.response.*;
import com.rms.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@SecurityRequirement(name="bearerAuth")
@Tag(name="Payments", description="Payment processing")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary="Process payment for an order")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(@Valid @RequestBody PaymentRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Payment processed", paymentService.processPayment(req)));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary="Get payment details by order ID")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success("Payment fetched", paymentService.getPaymentByOrderId(orderId)));
    }
}
