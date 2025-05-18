package com.tante.landlordtenant.service;

import com.tante.landlordtenant.models.Tenant;
import com.tante.landlordtenant.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TenantService
{
//    @Autowired
//    TenantRepository tenantRepository;

    //CREATE
//    public Tenant createTenant(Tenant tenant)
//    {
//        return tenantRepository.save(tenant);
//    }
//
//    //READ
//    public List<Tenant> getTenants()
//    {
//        return tenantRepository.findAll();
//    }
//
//    //READ ONE TENANT
//    public Optional<Tenant> getTenant(String tenantId)
//    {
//        return tenantRepository.findById(tenantId);
//    }
//
//
//    //DELETE
//    public void deleteTenant(String tenantId)
//    {
//        tenantRepository.deleteById(tenantId);
//    }
//
//    //UPDATE
//    public Tenant updateTenant(String tenantId, Tenant tenantDetails)
//    {
//        Tenant tenant = tenantRepository.findById(tenantId).get();
//        tenant.setFirstName(tenantDetails.getFirstName());
//        tenant.setSecondName(tenantDetails.getSecondName());
//        tenant.setPhoneNumber(tenantDetails.getPhoneNumber());
//        tenant.setEmailId(tenantDetails.getEmailId());
//        return tenantRepository.save(tenant);
//    }



}
