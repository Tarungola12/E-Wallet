package com.notification.otpcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.notification.service.Worker;

@RestController
@RequestMapping("/notification")
public class OTPController {

    @Autowired
   // @Qualifier("otpWorker")
    Worker worker;

    @PostMapping("/generate/otp/{email}")
    public String generateOTP(@PathVariable("email") String email){
        if (email== null || email.length()==0){
            return "NOT_SENT";
        }
        worker.sendNotification(email);
        return "OK";
    }




}