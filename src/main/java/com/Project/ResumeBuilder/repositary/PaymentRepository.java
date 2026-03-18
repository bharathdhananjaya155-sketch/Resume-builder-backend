package com.Project.ResumeBuilder.repositary;

import com.Project.ResumeBuilder.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Optional<Payment> findByRazerPayOrderId(String razerPayOrderId);

    Optional<Payment> findByRazerPayPaymentId(String razerPayPaymentId);

    List<Payment> findByUserIdOrderByCreatedAtDesc(Integer userId);

    List<Payment> findByStatus(String status);
}
