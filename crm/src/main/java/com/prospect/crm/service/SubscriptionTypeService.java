package com.prospect.crm.service;

import com.prospect.crm.constant.ErrorCode;
import com.prospect.crm.dto.ApiResponse;
import com.prospect.crm.exception.ResourceNotFoundException;
import com.prospect.crm.exception.ValidationException;
import com.prospect.crm.model.SubscriptionType;
import com.prospect.crm.repository.SubscriptionTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SubscriptionTypeService {
    private final SubscriptionTypeRepository subscriptionTypeRepository;

    public SubscriptionTypeService(SubscriptionTypeRepository subscriptionTypeRepository) {
        this.subscriptionTypeRepository = subscriptionTypeRepository;
    }

    public List<SubscriptionType> getAllSubscriptionTypes() {
        return subscriptionTypeRepository.findAll();
    }

    public SubscriptionType getSubscriptionTypeById(Long id) {
        return subscriptionTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ErrorCode.SUBSCRIPTION_TYPE_NOT_FOUND +" :"+ id));
    }

    public ResponseEntity<ApiResponse<SubscriptionType>> create(SubscriptionType subscriptionType) {
        if (!subscriptionTypeRepository.findByName(subscriptionType.getName()).isPresent()) {
            throw new ValidationException(ErrorCode.SUBSCRIPTION_TYPE_ALREADY_EXISTS + " : " + subscriptionType.getName());
        }
        subscriptionTypeRepository.save(subscriptionType);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(subscriptionType,"Subscription type created"));
    }
    public ResponseEntity<ApiResponse<SubscriptionType>> update(SubscriptionType subscriptionType) {
        SubscriptionType existingSubscriptionType = getSubscriptionTypeById(subscriptionType.getId());
        if (existingSubscriptionType == null) {
            throw new ResourceNotFoundException(ErrorCode.SUBSCRIPTION_TYPE_NOT_FOUND + " : " + subscriptionType.getName());
        }
        existingSubscriptionType.setName(subscriptionType.getName());
        existingSubscriptionType.setPrice(subscriptionType.getPrice());
        existingSubscriptionType.setDailyLimit(subscriptionType.getDailyLimit());
        subscriptionTypeRepository.save(existingSubscriptionType);
        return ResponseEntity.ok(ApiResponse.success(existingSubscriptionType,"Subscription type updated"));
    }
    public ResponseEntity<ApiResponse<SubscriptionType>> delete(Long id) {
        SubscriptionType subscriptionType = getSubscriptionTypeById(id);
        subscriptionType.setIsActive(false);
        subscriptionTypeRepository.save(subscriptionType);
        return ResponseEntity.ok(ApiResponse.success(subscriptionType,"Subscription type deleted"));
    }
}
