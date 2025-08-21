package com.prospect.crm.service;

import com.prospect.crm.constant.ErrorCode;
import com.prospect.crm.dto.ApiResponse;
import com.prospect.crm.exception.ResourceNotFoundException;
import com.prospect.crm.exception.ValidationException;
import com.prospect.crm.model.UserSubsInfo;
import com.prospect.crm.repository.UserSubsInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class UserSubsInfoService {
    private final UserSubsInfoRepository userSubsInfoRepository;

    public UserSubsInfoService(UserSubsInfoRepository userSubsInfoRepository) {
        this.userSubsInfoRepository = userSubsInfoRepository;
    }

    public List<UserSubsInfo> findAll() {
        return userSubsInfoRepository.findAll();
    }

    public List<UserSubsInfo> findAllActive() {
        return userSubsInfoRepository.findByIsActiveTrue().stream().toList();
    }

    public UserSubsInfo findById(Long id) {
        return userSubsInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_SUBS_INFO_NOT_FOUND.getMessage() + " :" + id ));
    }

    public UserSubsInfo findByIdAndActive(Long id) {
        return userSubsInfoRepository.findById(id)
                .filter(UserSubsInfo::getIsActive)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_SUBS_INFO_NOT_FOUND.getMessage() + " :" + id ));
    }

    public ResponseEntity<ApiResponse<UserSubsInfo>> create(UserSubsInfo userSubsInfo) {
        if (userSubsInfoRepository.findById(userSubsInfo.getId()).isPresent()){
            throw new ValidationException(ErrorCode.USER_SUBS_INFO_ALREADY_EXISTS.getMessage() + " :" + userSubsInfo.getId());
        }
        if(userSubsInfoRepository.findByUsersId(userSubsInfo.getUsersId())
                .filter(UserSubsInfo::getIsActive).isPresent()){
            throw new ValidationException(ErrorCode.USER_SUBS_INFO_ACTIVE_SUBS.getMessage() + " :" + userSubsInfo.getId());
        }
        userSubsInfo.setIsActive(true);
        userSubsInfo.setSubsStartDate(LocalDateTime.now());
        userSubsInfo.setSubsEndDate(LocalDateTime.now().plusDays(30));
        userSubsInfoRepository.save(userSubsInfo);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userSubsInfo,"User Subs created"));
    }

    public ResponseEntity<ApiResponse<UserSubsInfo>> update(Long id , UserSubsInfo userSubsInfo) {
        UserSubsInfo updatedUserSubsInfo = findById(id);
        updatedUserSubsInfo.setSubscriptionTypeId(userSubsInfo.getSubscriptionTypeId());

        if(userSubsInfoRepository.findByUsersId(userSubsInfo.getUsersId())
                .filter(UserSubsInfo::getIsActive).isPresent()){
            updatedUserSubsInfo.setSubsEndDate(updatedUserSubsInfo.getSubsEndDate().plusDays(30));
        }else {
            updatedUserSubsInfo.setSubsStartDate(LocalDateTime.now());
            updatedUserSubsInfo.setSubsEndDate(LocalDateTime.now().plusDays(30));
        }
        userSubsInfoRepository.save(updatedUserSubsInfo);
        return ResponseEntity.ok(ApiResponse.success(updatedUserSubsInfo,"User Subs updated"));
    }

    public ResponseEntity<ApiResponse<UserSubsInfo>> delete(Long id) {
        UserSubsInfo deletedUserSubsInfo = findById(id);
        deletedUserSubsInfo.setIsActive(false);
        userSubsInfoRepository.save(deletedUserSubsInfo);
        return ResponseEntity.ok(ApiResponse.success(deletedUserSubsInfo,"User Subs deleted"));
    }
}
