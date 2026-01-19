package com.transactionservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.commonservice.CommonConstants.CommonConstants;
import com.transactionservice.model.Transaction;
import com.transactionservice.model.TxnStatus;
import com.transactionservice.repository.TransactionRepository;
import com.transactionservice.request.TransactionRequest;
import com.transactionservice.response.TransactionResponse;

@Service
public class TransactionService {

	@Autowired
	TransactionRepository transactionRepository;

	@Autowired
	KafkaTemplate<String, String> kafkaTemplate;

	public String initiateTransaction(TransactionRequest transactionRequest, String senderMobileNo) {
		String receiverid = transactionRequest.getReceiver();
		double amount = transactionRequest.getTransferAmount();
		String purpose = transactionRequest.getPurpose();

		Transaction transaction = Transaction.builder().senderId(senderMobileNo).receiverId(receiverid).purpose(purpose)
				.transferAmount(amount).txnMessage("Transaction Initiated").txnStatus(TxnStatus.INITIATE).build();

		String txnid = UUID.randomUUID().toString();

		transaction.setTxnId(txnid);
		// Now At this point of time transaction is initiated and save in database.
		transactionRepository.save(transaction);

		// Send Data to Kafka means transaction send to kafka which is listen by wallet
		kafkaTemplate.send("TXN_TOPIC", createJsonObjectOfTransaction(transaction));
		return txnid;
	}

	public static String createJsonObjectOfTransaction(Transaction transaction) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(CommonConstants.SENDER_ID, transaction.getSenderId());
		jsonObject.put(CommonConstants.RECEIVER_ID, transaction.getReceiverId());
		jsonObject.put(CommonConstants.TRANSACTION_AMOUNT, transaction.getTransferAmount());
		jsonObject.put(CommonConstants.TRANSACTION_ID, transaction.getTxnId());

		return jsonObject.toString();

	}

	public void updateTransaction(String txnId, TxnStatus status, String message) {
		System.out.println("txnid: " + txnId);
		transactionRepository.updateTransaction(txnId, status, message);

	}

	public List<TransactionResponse> getTransactionResponse(String user) {
		List<Transaction> transactionList = transactionRepository.findBySenderIdOrReceiverId(user, user);

		List<TransactionResponse> ans = new ArrayList<>();

		for (Transaction t : transactionList) {
			TransactionResponse transactionResponse = new TransactionResponse();
			transactionResponse.setSentTo(t.getReceiverId());
			transactionResponse.setAmount(t.getTransferAmount());
			transactionResponse.setTxnTime(t.getCreatedOn());
			if (t.getSenderId().equals(user)) {
				transactionResponse.setTxnType("USER_DEBIT");
			} else {
				transactionResponse.setTxnType("USER_CREDIT");
			}
			ans.add(transactionResponse);
		}
		return ans;
	}
}