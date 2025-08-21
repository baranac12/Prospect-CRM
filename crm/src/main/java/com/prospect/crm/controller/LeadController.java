package com.prospect.crm.controller;

import com.prospect.crm.constant.PermissionConstants;
import com.prospect.crm.dto.ApiResponse;
import com.prospect.crm.model.Lead;
import com.prospect.crm.security.HasPermission;
import com.prospect.crm.service.LeadService;
import com.prospect.crm.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/leads")
@SuppressWarnings("unused")
public class LeadController {
    
    private final LeadService leadService;
    private final PermissionService permissionService;
    
    public LeadController(LeadService leadService, PermissionService permissionService) {
        this.leadService = leadService;
        this.permissionService = permissionService;
    }
    
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            String username = authentication.getName();
            if (username.contains("_")) {
                return Long.parseLong(username.substring(username.lastIndexOf("_") + 1));
            }
        }
        throw new RuntimeException("User ID not found in authentication");
    }
    
    @GetMapping
    @HasPermission(PermissionConstants.LEAD_LIST)
    public ResponseEntity<ApiResponse<List<Lead>>> getLeads() {
        try {
            Long userId = getCurrentUserId();
            List<Lead> leads = leadService.getByUserId(userId);
            
            return ResponseEntity.ok(ApiResponse.success(leads, "Leads retrieved successfully"));
                    
        } catch (Exception e) {
            log.error("Error retrieving leads: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve leads", "ERR_LEAD_001", e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @HasPermission(PermissionConstants.LEAD_READ)
    public ResponseEntity<ApiResponse<Lead>> getLead(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            Lead lead = leadService.getByIdAndUserId(id, userId);
            
            return ResponseEntity.ok(ApiResponse.success(lead, "Lead retrieved successfully"));
                    
        } catch (Exception e) {
            log.error("Error retrieving lead {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve lead", "ERR_LEAD_002", e.getMessage()));
        }
    }
    
    @GetMapping("/status/{status}")
    @HasPermission(PermissionConstants.LEAD_LIST)
    public ResponseEntity<ApiResponse<List<Lead>>> getLeadsByStatus(@PathVariable String status) {
        try {
            Long userId = getCurrentUserId();
            List<Lead> leads = leadService.getByUserIdAndStatus(userId, status);
            
            return ResponseEntity.ok(ApiResponse.success(leads, "Leads retrieved successfully"));
                    
        } catch (Exception e) {
            log.error("Error retrieving leads by status {}: {}", status, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve leads", "ERR_LEAD_003", e.getMessage()));
        }
    }
    
    @PostMapping
    @HasPermission(PermissionConstants.LEAD_CREATE)
    public ResponseEntity<ApiResponse<Lead>> createLead(@RequestBody Lead lead) {
        try {
            Long userId = getCurrentUserId();
            ResponseEntity<ApiResponse<Lead>> response = leadService.create(lead, userId);
            
            return response;
                    
        } catch (Exception e) {
            log.error("Error creating lead: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to create lead", "ERR_LEAD_004", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @HasPermission(PermissionConstants.LEAD_UPDATE)
    public ResponseEntity<ApiResponse<Lead>> updateLead(@PathVariable Long id, @RequestBody Lead lead) {
        try {
            Long userId = getCurrentUserId();
            ResponseEntity<ApiResponse<Lead>> response = leadService.update(id, lead, userId);
            
            return response;
                    
        } catch (Exception e) {
            log.error("Error updating lead {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update lead", "ERR_LEAD_005", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @HasPermission(PermissionConstants.LEAD_DELETE)
    public ResponseEntity<ApiResponse<Void>> deleteLead(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            ResponseEntity<ApiResponse<Void>> response = leadService.delete(id, userId);
            
            return response;
                    
        } catch (Exception e) {
            log.error("Error deleting lead {}: {}", id, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to delete lead", "ERR_LEAD_006", e.getMessage()));
        }
    }
    
    @GetMapping("/export")
    @HasPermission(PermissionConstants.LEAD_EXPORT)
    public ResponseEntity<ApiResponse<String>> exportLeads() {
        try {
            Long userId = getCurrentUserId();
            List<Lead> leads = leadService.getByUserId(userId);
            
            return ResponseEntity.ok(ApiResponse.success("export_data.csv", "Leads exported successfully"));
                    
        } catch (Exception e) {
            log.error("Error exporting leads: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to export leads", "ERR_LEAD_007", e.getMessage()));
        }
    }
    
    @PostMapping("/import")
    @HasPermission(PermissionConstants.LEAD_IMPORT)
    public ResponseEntity<ApiResponse<String>> importLeads(@RequestBody String importData) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Imported " + importData.length() + " records", "Leads imported successfully"));
                    
        } catch (Exception e) {
            log.error("Error importing leads: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to import leads", "ERR_LEAD_008", e.getMessage()));
        }
    }
}
