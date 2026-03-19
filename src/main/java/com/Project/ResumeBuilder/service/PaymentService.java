package com.Project.ResumeBuilder.service;

import com.Project.ResumeBuilder.dto.AuthResponse;
import com.Project.ResumeBuilder.entity.Payment;
import com.Project.ResumeBuilder.entity.User;
import com.Project.ResumeBuilder.repositary.PaymentRepository;
import com.Project.ResumeBuilder.repositary.UserRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.Project.ResumeBuilder.util.AppConstant.PREMIUM;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AuthService authService;
    private final UserRepository userRepository;

    @Value("${RAZORPAY_ID}")
    private String razerPayKeyId;

    @Value("${RAZORPAY_SECRET}")
    private String razerPayKeySecret;


    public Payment createOrder(Object principal, String planType) throws RazorpayException {
        AuthResponse response = authService.getProfile(principal);

        RazorpayClient razerPayClient = new RazorpayClient(razerPayKeyId, razerPayKeySecret);

        int amount = 99900; // Amount in paise (999 INR)
        String currency = "INR";
        String receipt = PREMIUM+"_" + UUID.randomUUID().toString().substring(0, 8);

        JSONObject orderRequest = new JSONObject();

        orderRequest.put("amount", amount);
        orderRequest.put("currency", currency);
        orderRequest.put("receipt", receipt);

        Order razerPayOrder = razerPayClient.orders.create(orderRequest);

        Payment newPayment = Payment.builder()
                .userId(response.getId())
                .razerPayOrderId(razerPayOrder.get("id"))
                .amount(amount)
                .planType(planType)
                .currency(currency)
                .receipt(receipt)
                .status("created")
                .build();

        return paymentRepository.save(newPayment);
    }

    public boolean verifyPyment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {

        try {
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", razorpayOrderId);
            attributes.put("razorpay_payment_id", razorpayPaymentId);
            attributes.put("razorpay_signature", razorpaySignature);

            boolean isValidSignature = Utils.verifyPaymentSignature(attributes, razerPayKeySecret);

            if (isValidSignature) {
                Payment payment = paymentRepository.findByRazerPayOrderId(razorpayOrderId)
                        .orElseThrow(() -> new RuntimeException("Payment not found"));

                payment.setRazerPayPaymentId(razorpayPaymentId);
                payment.setRazerPaySignature(razorpaySignature);
                payment.setStatus("paid");
                paymentRepository.save(payment);

                upgradeUserSubscription(payment.getUserId(), payment.getPlanType());
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Error verifying the payment", e);
            return false;
        }
    }

    private void upgradeUserSubscription(Integer userId, String planType) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setSubscription(planType);
        userRepository.save(existingUser);
        log.info("User {} upgraded to plan {}", userId, planType);
    }

    public List<Payment> getUserPayment(Object principal) {

        AuthResponse authResponse =authService.getProfile(principal);

        return paymentRepository.findByUserIdOrderByCreatedAtDesc(authResponse.getId());
    }

    public Payment getPaymentDetails(String orderId) {
        return paymentRepository.findByRazerPayOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }
}
