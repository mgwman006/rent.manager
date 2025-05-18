package com.tante.landlordtenant.controller;

import com.tante.landlordtenant.models.Tenant;
import com.tante.landlordtenant.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class TenantController
{
    @Autowired
    TenantService tenantService;

//    @RequestMapping(value="/tenant", method = RequestMethod.POST)
//    public Tenant createTenant (@RequestBody Tenant tenant)
//    {
//        return tenantService.createTenant(tenant);
//    }
//
//    @RequestMapping(value = "/tenants", method = RequestMethod.GET)
//    public List<Tenant> getTenants()
//    {
//        return tenantService.getTenants();
//    }
//
//    @RequestMapping(value="/tenant", method = RequestMethod.GET)
//    public Tenant getTenant(@RequestParam(value="tenantId") String tenantId)
//    {
//        Tenant tenant1;
//        Optional<Tenant> optionalTenant =tenantService.getTenant(tenantId);
//        if (optionalTenant.isPresent())
//        {
//            tenant1=optionalTenant.get();
//        }
//        else
//        {
//            tenant1 = null;
//        }
//
//        return tenant1;
//    }
//
//    @RequestMapping(value="/tenant", method=RequestMethod.DELETE)
//    public void deleteTenant(@RequestParam(value = "tenantId") String tenantId)
//    {
//        tenantService.deleteTenant(tenantId);
//    }
//
//    @RequestMapping(value="/tenant", method=RequestMethod.PUT)
//    public Tenant updateTenant(@RequestBody Tenant tenantDetails)
//    {
//        return tenantService.updateTenant(tenantDetails.getTenantId(), tenantDetails);
//    }


}
