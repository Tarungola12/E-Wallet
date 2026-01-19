package com.walletservice.model;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.commonservice.commonmodel.UserIdentifier;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "wallet")
@Builder
public class Wallet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	int id;
	
	@Column(unique = true)
	int userId;
	
	@Column(unique = true)
	String mobileNo;
	
	@Enumerated(EnumType.STRING)
	WalletStatus walletStatus;

	@Enumerated(EnumType.STRING)
	UserIdentifier userIdentifier;

	String userIdentifierValue;
	double balance;

	@CreationTimestamp
	Date createdOn;
	@UpdateTimestamp
	Date updatedOn;
}