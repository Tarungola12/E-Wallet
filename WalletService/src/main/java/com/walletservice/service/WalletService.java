package com.walletservice.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.commonservice.CommonConstants.CommonConstants;
import com.commonservice.commonmodel.UserIdentifier;
import com.walletservice.model.Wallet;
import com.walletservice.model.WalletStatus;
import com.walletservice.repository.WalletRepository;

import jakarta.transaction.Transactional;

@Service
public class WalletService {

	@Value("${wallet.initial.amount}")
	private String walletBalance;

	@Autowired
	WalletRepository walletRepository;

	@Autowired
	KafkaTemplate<String, String> kafkaTemplate;


	public void createWalletAccount(String mobile, UserIdentifier userIdentifier, String userIdentifierValue,
			int userid) {

		Wallet wallet = Wallet.builder().userId(userid).mobileNo(mobile).userIdentifier(userIdentifier)
				.userIdentifierValue(userIdentifierValue).walletStatus(WalletStatus.ACTIVE)
				.balance(Double.parseDouble(walletBalance)).build();

		walletRepository.save(wallet);

		System.out.println("Wallet Account created");

	}
	

	
	// Updating the wallet balance
    @Transactional
    public void updateWalletBalance(String senderMobileNo, String receiverId, double amount, String txnId){
    	
    	String status="FAILED";
    	String message="SOME PROBLEM OCCURED";
    	
    	// Search If wallet exist of both send and receiver
        Wallet senderWallet = walletRepository.findByMobileNo(senderMobileNo);
        Wallet receiverWallet = walletRepository.findByMobileNo(receiverId);

        if (receiverWallet!=null && receiverWallet.getWalletStatus().equals(WalletStatus.ACTIVE)){
            if (senderWallet.getBalance() > amount){
                System.out.println("going to update the balance");
                if (updateWalletBalance(senderMobileNo,receiverId,amount)){
                    status = "SUCCESS";
                    message = "Transaction is success";
                }else {
                    status = "PENDING";
                    message = "Transaction is pending";
                }
            }
            else {
                status = "FAILED";
                message = "Insufficient Balance IN Sender Wallet";
            }
        }else if (receiverWallet==null){
            status = "FAILED";
            message = "receiver wallet doesn't exist";
        }else {
            status = "FAILED";
            message = "receiver account is blocked";
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(CommonConstants.TRANSACTION_AMOUNT,amount);
        jsonObject.put(CommonConstants.TRANSACTION_ID,txnId);
        jsonObject.put(CommonConstants.SENDER_ID,senderMobileNo);
        jsonObject.put(CommonConstants.RECEIVER_ID,receiverId);
        jsonObject.put(CommonConstants.TRANSACTION_STATUS, status);
        jsonObject.put(CommonConstants.TRANSACTION_MESSAGE,message);

        kafkaTemplate.send("TXN_UPDATE_TOPIC", jsonObject.toString());

        System.out.println("Updated data send to Kafka: "+jsonObject.toString());

    }

    // Update the Wallet Balance
    @Transactional
    public boolean updateWalletBalance(String senderMobileNo, String receiver, double amount){
        boolean isUpdated = true;
        System.out.println("sender id : "+senderMobileNo);
        System.out.println("receiver id : "+receiver);
        try {
            walletRepository.updateSenderWalletBalance(senderMobileNo, amount);
            walletRepository.updateWalletBalance(receiver, amount);
        }
        catch (Exception exception){
            System.out.println("Some exception Happend when updating a balance in wallet of sender or maybe receiver");
            isUpdated = false;
        }
        return isUpdated;
    }


    public String getWalletBalance(String username){
      double balance =  walletRepository.findByMobileNo(username).getBalance();
      return Double.toString(balance);
    }
	

}
