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
    public ResponseEntity<ApiResponse<List<SubscriptionType>>> getAll() {
        return ResponseEntity.ok().body(ApiResponse.success(subscriptionTypeService.getAll(),"Subscription type retrieved successfully"));
    }
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<SubscriptionType>>> getAllActive() {
        return ResponseEntity.ok().body(ApiResponse.success(subscriptionTypeService.getAllActive(), "Subscription types retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionType>> getById(@PathVariable Long id) {
        return ResponseEntity.ok().body(ApiResponse.success(subscriptionTypeService.getById(id), "Subscription type retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SubscriptionType>> create(@RequestBody SubscriptionType subscriptionType) {
        return subscriptionTypeService.create(subscriptionType);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionType>> update(@PathVariable Long id, @RequestBody SubscriptionType subscriptionType) {
        return subscriptionTypeService.update(id,subscriptionType);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<SubscriptionType>> delete(@PathVariable Long id) {
        return subscriptionTypeService.delete(id);
    }
}
