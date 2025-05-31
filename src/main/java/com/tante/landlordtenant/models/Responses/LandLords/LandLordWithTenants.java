package com.tante.landlordtenant.models.Responses.LandLords;

import com.tante.landlordtenant.models.Responses.Tenants.TenantResponseDto;

import java.util.List;

public record LandLordWithTenants(
        LandLordResponseDto landLordDetails,
        List<TenantResponseDto> tenants
) {

}
