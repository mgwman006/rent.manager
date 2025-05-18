package com.tante.landlordtenant.models;

import com.tante.landlordtenant.models.Landlord.Landlord;

import javax.persistence.*;
import java.util.Date;

public class Contract
{

    @Column(name = "contract_id")
    String contractId;

    @Column(name = "start_date")
    Date startDate;

    @Column(name = "end_date")
    Date endDate;

    @Column(name = "amount")
    int amount;

    Landlord landlord;

    Tenant tenant;

    Apartment apartment;

    public Contract() {
    }

    public Contract(String contractId, Date startDate, Date endDate, int amount, Landlord landlord, Tenant tenant, Apartment apartment) {
        this.contractId = contractId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.amount = amount;
        this.landlord = landlord;
        this.tenant = tenant;
        this.apartment = apartment;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Landlord getLandlord() {
        return landlord;
    }

    public void setLandlord(Landlord landlord) {
        this.landlord = landlord;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
    }
}
