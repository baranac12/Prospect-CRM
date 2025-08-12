package com.prospect.crm.controller;

import com.prospect.crm.dto.ApiResponse;
import com.prospect.crm.model.Lead;
import com.prospect.crm.service.LeadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/lead")
public class LeadController {
    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Lead>>> getAll() {
        return ResponseEntity.ok().body(ApiResponse.success(leadService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Lead>> getById(@PathVariable Long id) {
        return ResponseEntity.ok().body(ApiResponse.success(leadService.getById(id)));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<Lead>>> getActive() {
        return ResponseEntity.ok().body(ApiResponse.success(leadService.getActive()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Lead>> create(@RequestBody Lead lead) {
        return leadService.create(lead);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Lead>> update(@PathVariable Long id, @RequestBody Lead lead) {
        return leadService.update(id, lead);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        return leadService.delete(id);
    }
}
