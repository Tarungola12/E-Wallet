package com.userservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.userservice.model.User;
import com.userservice.request.OTPVerificationRequest;
import com.userservice.request.UserCreationRequest;
import com.userservice.response.OTPVerificationResponse;
import com.userservice.response.UserCreationAcknowledgement;
import com.userservice.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user-service")
@CrossOrigin(origins = "*")
public class UserController {

	@Autowired
	UserService userService;

	@PostMapping("/t")
	public String postMethodName() {
		return "ok";
	}

	
	// Functionality : In this api we take user information and save the user in the database
	@PostMapping("/onboard/user")
	public ResponseEntity<UserCreationAcknowledgement> onboardNewUser(
			@RequestBody @Valid UserCreationRequest userCreationRequest) {
		UserCreationAcknowledgement userCreationAcknowledgement = new UserCreationAcknowledgement();

		if (!userCreationRequest.getEmail().contains("@")) {
			userCreationAcknowledgement.setMessage("Please pass valid email");
		} else if (!userCreationRequest.getDob().contains("/")) {
			userCreationAcknowledgement.setMessage("Please pass correct DOB");
		} else if (userCreationRequest.getMobileNo().length() > 9 && userCreationRequest.getMobileNo().length() < 14) {
			userCreationAcknowledgement.setMessage("Invalid Mobile No");
		}

		userCreationAcknowledgement = userService.onboardNewUser(userCreationRequest);
		return new ResponseEntity<>(userCreationAcknowledgement, HttpStatus.OK);
		
	}

	@PostMapping("/validate/otp")
	public ResponseEntity<OTPVerificationResponse> validateOTPAndSaveUser(
			@RequestBody OTPVerificationRequest otpVerificationRequest) {
		User user = userService.validateAndSaveUser(otpVerificationRequest);

		OTPVerificationResponse otpVerificationResponse = new OTPVerificationResponse();

		otpVerificationResponse.setStatus("SUCCESS");

		if (user == null) {
			otpVerificationResponse.setStatus("FAILED");
		}

		otpVerificationResponse.setSavedUser(user);
		return new ResponseEntity<>(otpVerificationResponse, HttpStatus.OK);

	}

	
	@GetMapping("/validate/user/{userId}")
	public String validateUser(@PathVariable("userId") String userId) {
		System.out.println("Request recived");
		return userService.fetchAndReturnUser(userId);
	}

}
