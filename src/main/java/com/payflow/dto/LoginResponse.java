package com.payflow.dto;

//when only returning message instead of using jwt token
//public record LoginResponse(Long userID,String email,String message) {
//}


public record LoginResponse(
        String accessToken,
        String refreshToken
) {

}
