 package com.prospect.crm.controller;

 import com.prospect.crm.dto.ApiResponse;
 import com.prospect.crm.model.UserSubsInfo;
 import com.prospect.crm.service.UserSubsInfoService;
 import lombok.extern.slf4j.Slf4j;
 import org.springframework.http.ResponseEntity;
 import org.springframework.security.core.userdetails.User;
 import org.springframework.web.bind.annotation.*;

 import java.util.List;

 @Slf4j
 @RestController
 @RequestMapping("/v1/user/subs")
public class UserSubsInfoController {
     private final UserSubsInfoService userSubsInfoService;

     public UserSubsInfoController(UserSubsInfoService userSubsInfoService) {
         this.userSubsInfoService = userSubsInfoService;
     }

     @GetMapping
     public ResponseEntity<ApiResponse<List<UserSubsInfo>>> getAllUserSubsInfo() {
         return ResponseEntity.ok().body(ApiResponse.success(userSubsInfoService.findAll(),"User Subs Info retrieved successfully"));
     }

     @GetMapping("/{id}")
     public ResponseEntity<ApiResponse<UserSubsInfo>> getUserSubsInfoById(@PathVariable("id") Long id) {
         return ResponseEntity.ok().body(ApiResponse.success(userSubsInfoService.findById(id),"User Subs Info retrieved successfully"));
     }
     @PostMapping
     public ResponseEntity<ApiResponse<UserSubsInfo>> addUserSubsInfo(@RequestBody UserSubsInfo userSubsInfo) {
         return userSubsInfoService.create(userSubsInfo);
     }
     @PutMapping("/{id}")
     public ResponseEntity<ApiResponse<UserSubsInfo>> updateUserSubsInfo(@PathVariable("id") Long id, @RequestBody UserSubsInfo userSubsInfo) {
         userSubsInfo.setId(id);
         return userSubsInfoService.update(userSubsInfo);
     }
     @DeleteMapping("/{id}")
     public ResponseEntity<ApiResponse<UserSubsInfo>> deleteUserSubsInfo(@PathVariable("id") Long id) {
         return userSubsInfoService.delete(id);
     }
 }
