package com.Project.ResumeBuilder.controller;

import com.Project.ResumeBuilder.entity.Payment;
import com.Project.ResumeBuilder.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.Project.ResumeBuilder.util.AppConstant.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(PAYMENT_CONTROLLER)
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping(CREATE_ORDER)
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, String> request,
                                                           Authentication authentication) throws Exception {
        String planType = request.get("planType");
        if (!PREMIUM.equalsIgnoreCase(planType)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "invalid plan type"));
        }
        Payment payment = paymentService.createOrder(authentication.getPrincipal(), planType);

        Map<String, Object> response = new HashMap<>();

        response.put("orderId", payment.getRazerPayOrderId());
        response.put("amount", payment.getAmount());
        response.put("currency", payment.getCurrency());
        response.put("receipt", payment.getReceipt());

        return ResponseEntity.ok(response);
    }

    @PostMapping(VERIFY_PAYMENT)
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> request) {
        String razorpayOrderId = request.get("razorpay_order_id");
        String razorpayPaymentId=request.get("razorpay_payment_id");
        String razorpaySignature=request.get("razorpay_signature");

        if(Objects.isNull(razorpayPaymentId) || Objects.isNull(razorpayOrderId) || Objects.isNull(razorpaySignature)){
            return ResponseEntity.badRequest().body(Map.of("message","Missing payment details"));
        }

        boolean isValid =paymentService.verifyPyment(razorpayOrderId,razorpayPaymentId,razorpaySignature);

        if(isValid){
            return ResponseEntity.ok(Map.of("message","Payment verified successfully",
                    "status","success"));
        }
        else{
            return ResponseEntity.badRequest().body(Map.of("message","Payment verification failed"));
        }

    }

    @GetMapping(HISTORY)
    public ResponseEntity<?> getPayments(Authentication authentication){
       List<Payment> payment= paymentService.getUserPayment(authentication.getPrincipal());

       return ResponseEntity.ok(payment);
    }

    @GetMapping(ORDER)
    public ResponseEntity<?> getOrderDetails(@PathVariable String orderId){

        Payment payment =paymentService.getPaymentDetails(orderId);
        return ResponseEntity.ok(payment);
    }
}
