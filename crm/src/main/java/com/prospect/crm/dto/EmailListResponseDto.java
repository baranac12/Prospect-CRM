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
public class EmailListResponseDto {
    private List<EmailListItemDto> emails;
    private String nextPageToken;
    private Integer resultSizeEstimate;
    private String provider; // GOOGLE, MICROSOFT
    private String label; // INBOX, SENT, etc.
    private Integer totalCount;
}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class EmailListItemDto {
    private String id;
    private String threadId;
    private String subject;
    private String from;
    private String snippet;
    private String receivedDate;
    private Boolean isRead;
    private Boolean isStarred;
    private Boolean isImportant;
    private List<String> labels;
    private Boolean hasAttachments;
} 