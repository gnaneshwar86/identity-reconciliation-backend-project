package com.gnaneshwar.identity_reconciliation.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class IdentifyResponse {

    private ContactData contact;

    @Data
    @Builder
    public static class ContactData {
        private Long primaryContactId;
        private List<String> emails;
        private List<String> phoneNumbers;
        private List<Long> secondaryContactIds;
    }
}