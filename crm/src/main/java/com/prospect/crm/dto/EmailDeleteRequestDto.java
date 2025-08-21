package com.prospect.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailDeleteRequestDto {
    private String provider; // GOOGLE, MICROSOFT
    private List<String> emailIds; // Silinecek email ID'leri
    private Boolean permanentDelete; // Kalıcı silme (true) veya çöp kutusuna taşıma (false)
} 