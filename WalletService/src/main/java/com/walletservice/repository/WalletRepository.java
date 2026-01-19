package com.walletservice.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.walletservice.model.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet,Integer> {

    Wallet findByMobileNo(String mobile);

    @Query("update wallet w set w.balance=w.balance+:amount where w.mobileNo=:account")
    @Transactional
    @Modifying
    void updateWalletBalance(String account, double amount);

    @Query("update wallet w set w.balance=w.balance-:amount where w.mobileNo=:sendMobileNo")
    @Transactional
    @Modifying
    void updateSenderWalletBalance(String sendMobileNo, double amount);

}