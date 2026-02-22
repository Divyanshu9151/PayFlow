package com.payflow.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest (@NotBlank(message = "Body must be present") String refreshToken){
}
