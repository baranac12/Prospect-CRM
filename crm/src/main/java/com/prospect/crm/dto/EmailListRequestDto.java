package com.prospect.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailListRequestDto {
    private String provider; // GOOGLE, MICROSOFT
    private String label; // INBOX, SENT, DRAFT, SPAM, TRASH, etc.
    private String query; // Arama sorgusu
    private Integer maxResults; // Maksimum sonuç sayısı
    private String pageToken; // Sayfalama token'ı
    private Boolean includeSpamTrash; // Spam ve çöp kutusunu dahil et
    private String orderBy; // internalDate, date, from, subject
    private String sortOrder; // ascending, descending
} 