package com.userservice.request;


import com.commonservice.commonmodel.UserIdentifier;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreationRequest {

    @NotNull
    String name;
    @NotNull
    String email;
    @NotNull
    String mobileNo;
    @NotNull
    String password;
    @NotNull
    String dob;
    @NotNull
    UserIdentifier userIdentifier;
    @NotNull
    String userIdentifierValue;



}