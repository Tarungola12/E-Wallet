package com.notification.service;

import org.springframework.stereotype.Component;

@Component
public interface Worker {

     void sendNotification(String email);
}