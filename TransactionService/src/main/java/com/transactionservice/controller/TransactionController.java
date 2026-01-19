package com.transactionservice.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.transactionservice.model.Transaction;
import com.transactionservice.model.TxnStatus;
import com.transactionservice.repository.TransactionRepository;
import com.transactionservice.request.TransactionRequest;
import com.transactionservice.response.TransactionResponse;
import com.transactionservice.service.TransactionService;

@RestController
@RequestMapping("/transaction-service")
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionRepository transactionRepository;

    @Autowired
    TransactionService transactionService;

    TransactionController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // In this api we create transaction request object and make transaction
    @PostMapping("/create/txn")
    public ResponseEntity<String> initiateTransaction(@RequestBody TransactionRequest transactionRequest){
        if (transactionRequest==null){
            return new ResponseEntity<>("Invalid Request", HttpStatus.OK);
        }
        
        // take the send id{Means mobileNumber of sender} from userDetails Of Security Context Holder
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        String txnId = transactionService.initiateTransaction(transactionRequest, userDetails.getUsername());
//       
//        int i = 0;
//        String txnStatus = checkTransactionStatus(txnId);
//        while( (txnStatus.equals(TxnStatus.PENDING.toString()) || txnStatus.equals(TxnStatus.INITIATE.toString())) && i < 3) {
//        	try {
//        		Thread.sleep(3000);
//				i++;
//				txnStatus = checkTransactionStatus(txnId);
//			} catch (Exception e) {
//				System.out.println("Thread Got Interupted.");
//				e.printStackTrace();
//			}
//        }
//        
        
        return new ResponseEntity<>(txnId, HttpStatus.OK);
    }

//    public String checkTransactionStatus(String txnId) {
//    		System.out.println("Transaction id which is going : "+txnId);
//    		Transaction  transaction = transactionRepository.findByTxnId(txnId);
//    		System.out.println(transaction);
//    		System.out.println("transaction status : " + transaction.getTxnStatus());
//    		if(transaction.getTxnStatus().toString() == TxnStatus.PENDING.toString() || transaction.getTxnStatus().toString() == TxnStatus.INITIATE.toString()) {
//    			return TxnStatus.PENDING.toString();
//    		}
//    		return transaction.getTxnStatus().toString();
//    }
//    

    @GetMapping("/get/transaction/history")
    public List<TransactionResponse> getTransactionHistory(){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentUser = userDetails.getUsername();

        return transactionService.getTransactionResponse(currentUser);
    }
}