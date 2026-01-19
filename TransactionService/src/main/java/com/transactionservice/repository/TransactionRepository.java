package com.transactionservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.transactionservice.model.Transaction;
import com.transactionservice.model.TxnStatus;

import jakarta.transaction.Transactional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Integer> {

    @Query("update transaction t set t.txnStatus=:txnStatus, t.txnMessage=:txnMessage where t.txnId=:txnId")
    @Transactional
    @Modifying/*(clearAutomatically = true, flushAutomatically = true)*/
    void updateTransaction(String txnId, TxnStatus txnStatus, String txnMessage);

    List<Transaction> findBySenderIdOrReceiverId(String sender, String receiver);
    
//    @Query(value = "select * from Transaction where txn_id=:txnId",nativeQuery = true)
//    Transaction transactionUsingTransactionId(String txnId);
    
//    Transaction findByTxnId(String txnId);
    
}