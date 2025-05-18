package com.tante.landlordtenant.models.Entities;

import com.tante.landlordtenant.models.Entities.Landlord.Landlord;
import jakarta.persistence.Column;
import jakarta.persistence.Id;


public class PaymentDetails
{
    @Id
    @Column(name = "payment_details_id")
    String paymentDetailsId;

    @Column(name = "bank_name")
    String bankName;

    @Column(name="account_name")
    String accountName;

    @Column(name = "account_number")
    String accountNumber;

    Landlord landlord;

    public PaymentDetails() {
    }

    public PaymentDetails(String paymentDetailsId, String bankName, String accountName, String accountNumber, Landlord landlord) {
        this.paymentDetailsId = paymentDetailsId;
        this.bankName = bankName;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.landlord = landlord;
    }

    public String getPaymentDetailsId() {
        return paymentDetailsId;
    }

    public void setPaymentDetailsId(String paymentDetailsId) {
        this.paymentDetailsId = paymentDetailsId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Landlord getLandlord() {
        return landlord;
    }

    public void setLandlord(Landlord landlord) {
        this.landlord = landlord;
    }
}
