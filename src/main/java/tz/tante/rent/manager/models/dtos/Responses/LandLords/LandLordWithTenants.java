package tz.tante.rent.manager.models.dtos.Responses.LandLords;

import tz.tante.rent.manager.models.dtos.Responses.Tenants.TenantResponseDto;

import java.util.List;

public record LandLordWithTenants(
        LandLordResponseDto landLordDetails,
        List<TenantResponseDto> tenants
) {

}
