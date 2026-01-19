package com.userservice.response;

import com.userservice.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OTPVerificationResponse {

    User savedUser;
    String status;
}