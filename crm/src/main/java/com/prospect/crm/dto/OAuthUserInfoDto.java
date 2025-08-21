package com.prospect.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OAuthUserInfoDto {
    private String provider; // google, microsoft
    private String email;
    private String name;
    private String surname;
    private String givenName; // Google'dan gelen first name
    private String familyName; // Google'dan gelen last name
    private String displayName; // Microsoft'dan gelen display name
    private String picture; // Profile picture URL
    private String locale;
    private String sub; // OAuth provider'dan gelen unique ID
} 