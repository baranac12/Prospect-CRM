package com.prospect.crm.repository;

import com.prospect.crm.model.Payment;
import com.prospect.crm.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Users usersId);
    List<Payment> findByStatus(String status);
    List<Payment> findByPaymentMethod(String paymentMethod);
} 