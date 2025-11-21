package com.vocabapp.dto;

import lombok.Data;

@Data
public class TokenRefreshRequest {
    private String refreshToken;
}
