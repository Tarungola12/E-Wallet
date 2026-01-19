package com.notification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.notification.redis.RedisUtil;

@Component
public class OTPWorker implements Worker {

	@Autowired
	JavaMailSender javaMailSender;

	@Autowired
	RedisUtil redisUtil;

	@Override
	public void sendNotification(String email) {
		String otp = generateOTP();
		System.out.println("Generated OTP Value in notification service : " + otp);
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setSubject("OTP Verification");
		simpleMailMessage.setText("Your one time OTP for account creation is " + otp + " It is valid for 5 minutes");
		simpleMailMessage.setFrom("tarungola205@gmail.com");
		simpleMailMessage.setTo(email);

		// save otp
		redisUtil.setData(email + "OTP", otp);
		System.out.println("OTP saved in Redis");

		javaMailSender.send(simpleMailMessage);
		System.out.println("OTP Sent");
	}

	// Generating the otp :
	public String generateOTP() {
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 1; i <= 6; i++) {
			int num = (int) (Math.random() * 10);
			stringBuilder.append(num);
		}
		return stringBuilder.toString();
	}

}