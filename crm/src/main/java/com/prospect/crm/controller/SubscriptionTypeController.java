package com.prospect.crm.controller;

import com.prospect.crm.dto.ApiResponse;
import com.prospect.crm.model.SubscriptionType;
import com.prospect.crm.service.SubscriptionTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/subs")
public class SubscriptionTypeController {
    private final SubscriptionTypeService subscriptionTypeService;

    public SubscriptionTypeController(SubscriptionTypeService subscriptionTypeService) {
        this.subscriptionTypeService = subscriptionTypeService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubscriptionType>>> getAllSubscriptionTypes() {
        return ResponseEntity.ok(ApiResponse.success(subscriptionTypeService.getAllSubscriptionTypes(), "Subscription types retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionType>> getSubscriptionTypesId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(subscriptionTypeService.getSubscriptionTypeById(id), "Subscription type retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SubscriptionType>> addSubscriptionType(@RequestBody SubscriptionType subscriptionType) {
        return subscriptionTypeService.create(subscriptionType);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionType>> updateSubscriptionType(@PathVariable Long id, @RequestBody SubscriptionType subscriptionType) {
        subscriptionType.setId(id);
        return subscriptionTypeService.update(subscriptionType);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionType>> deleteSubscriptionType(@PathVariable Long id) {
        return subscriptionTypeService.delete(id);
    }
}
