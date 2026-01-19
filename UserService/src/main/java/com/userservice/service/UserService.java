package com.userservice.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.commonservice.CommonConstants.CommonConstants;
import com.userservice.feign.NotificationFeign;
import com.userservice.model.RoleType;
import com.userservice.model.User;
import com.userservice.model.UserStatus;
import com.userservice.redis.RedisUtil;
import com.userservice.repository.UserRepository;
import com.userservice.request.OTPVerificationRequest;
import com.userservice.request.UserCreationRequest;
import com.userservice.response.UserCreationAcknowledgement;

@Service
public class UserService {

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	NotificationFeign notificationFeign;

	@Autowired
	RedisUtil redisUtil;

	@Autowired
	KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	UserRepository userRepository;

	// In this method we create user and send otp to kafka to 
	public UserCreationAcknowledgement onboardNewUser(UserCreationRequest userCreationRequest) {

		// Here We Create User
		User user = User.builder().name(userCreationRequest.getName()).email(userCreationRequest.getEmail())
				.mobileNo(userCreationRequest.getMobileNo()).dob(userCreationRequest.getDob())
				.userIdentifier(userCreationRequest.getUserIdentifier())
				.userIdentifierValue(userCreationRequest.getUserIdentifierValue()).userStatus(UserStatus.ACTIVE)
				.password(passwordEncoder.encode(userCreationRequest.getPassword())).role(RoleType.NORMAL).build();

		// Call For Otp To Notification Service
		String message = notificationFeign.sendOTP(userCreationRequest.getEmail());

		// Create Response Object of User
		UserCreationAcknowledgement userCreationAcknowledgement = new UserCreationAcknowledgement();
		if ("OK".equals(message)) {
			userCreationAcknowledgement
					.setMessage("OTP Sent Pls Hit this end point '/user-service/validate/otp' for validate yourself ");
			userCreationAcknowledgement.setMobileNo(user.getMobileNo());
			userCreationAcknowledgement.setStatus("SUCCESS");
			redisUtil.setData(user.getMobileNo() + "USER", user);
			System.out.println("OTP sent successfully and data saved in cache");

		} else {
			userCreationAcknowledgement.setMessage("OTP Not Sent");
			userCreationAcknowledgement.setMobileNo(user.getMobileNo());
			userCreationAcknowledgement.setStatus("Failed");
		}
		
		return userCreationAcknowledgement;
	}

	// In this method we validate otp and save user
	public User validateAndSaveUser(OTPVerificationRequest otpVerificationRequest) {

		String otp = otpVerificationRequest.getOtp();
		String key = otpVerificationRequest.getEmail() + "OTP";

		// OTP from redis
		System.out.println(key);
		String savedOTP = redisUtil.getOTP(key);

		if (otp.equals(savedOTP)) {
			
			// fetch the data from redis and save in database
			String userKey = otpVerificationRequest.getMobile() + "USER";
			User user = redisUtil.getData(userKey);
			User savedUser = userRepository.save(user);
			
			
			// Now the Data is Send To Kafka for Welcome Notification to user on email :
			String jsonData = createKafkaData(user);
			kafkaTemplate.send(CommonConstants.USER_CREATION_TOPIC, jsonData);
			System.out.println("Data send to kafka");

			return savedUser;
		} else {
			return null;
		}
	}

	// create kafka object to make user to string{json}
	public String createKafkaData(User user) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(CommonConstants.USER_ID, user.getId());
		jsonObject.put(CommonConstants.USER_NAME, user.getName());
		jsonObject.put(CommonConstants.USER_EMAIL, user.getEmail());
		jsonObject.put(CommonConstants.USER_MOBILE, user.getMobileNo());
		jsonObject.put(CommonConstants.USER_IDENTIFIER, user.getUserIdentifier());
		jsonObject.put(CommonConstants.USER_IDENTIFIER_VALUE, user.getUserIdentifierValue());

		return jsonObject.toString();
	}

	public String fetchAndReturnUser(String username) {
		User user = userRepository.findByEmail(username);
		System.out.println("User information in User Table : "+ user);
		if (user == null) {
			return null;
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(CommonConstants.USER_EMAIL, user.getEmail());
		jsonObject.put(CommonConstants.USER_MOBILE, user.getMobileNo());
		jsonObject.put(CommonConstants.USER_PASSWORD, user.getPassword());

		return jsonObject.toString();
	}

}
