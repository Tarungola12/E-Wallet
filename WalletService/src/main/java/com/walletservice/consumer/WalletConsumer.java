package com.walletservice.consumer;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.commonservice.CommonConstants.CommonConstants;
import com.commonservice.commonmodel.UserIdentifier;
import com.walletservice.service.WalletService;

@Component
public class WalletConsumer {

	@Autowired
	WalletService walletService;

	// Listen data from user service for create wallet for user
	@KafkaListener(topics = "USER_CREATION_TOPIC", groupId = "WALLET_GROUP")
	public void listenNewlyCreatedUser(String data) {
		System.out.println("Data Consumed: " + data);
		JSONObject jsonObject = new JSONObject(data);
		int userId = jsonObject.optInt(CommonConstants.USER_ID);
		String mobileNo = jsonObject.optString(CommonConstants.USER_MOBILE);
		String userIdentifierValue = jsonObject.optString(CommonConstants.USER_IDENTIFIER_VALUE);
		UserIdentifier userIdentifier = jsonObject.optEnum(UserIdentifier.class, CommonConstants.USER_IDENTIFIER);

		walletService.createWalletAccount(mobileNo, userIdentifier, userIdentifierValue, userId);

	}
	
	
	// Data listen by transaction service for happening transaction
	@KafkaListener(topics = "TXN_TOPIC", groupId = "txn-create-group") 
	public void listenTransactions(String data) {
		System.out.println("txn data consumed: " + data);
		
		JSONObject jsonObject = new JSONObject(data);
		String senderId = jsonObject.optString(CommonConstants.SENDER_ID);
		String receiverId = jsonObject.optString(CommonConstants.RECEIVER_ID);
		String txnid = jsonObject.optString(CommonConstants.TRANSACTION_ID);
		double txnAmount = jsonObject.optDouble(CommonConstants.TRANSACTION_AMOUNT);
		
		walletService.updateWalletBalance(senderId, receiverId, txnAmount, txnid);

	}

}

