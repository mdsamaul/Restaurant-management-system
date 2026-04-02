package com.rms.dto.request;
import com.rms.entity.Payment;
import jakarta.validation.constraints.*;
import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentRequest {
    @NotNull private Long orderId;
    @NotNull private Payment.PaymentMethod paymentMethod;
    private String transactionRef;
}
