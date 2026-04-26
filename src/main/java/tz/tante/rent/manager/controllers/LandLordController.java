//package tz.tante.rent.manager.controllers;
//
//import tz.tante.rent.manager.models.Requests.LandLords.LandLordRequestDto;
//import tz.tante.rent.manager.models.Requests.Tenants.TenantRequestDto;
//import tz.tante.rent.manager.models.dtos.Responses.LandLords.LandLordResponseDto;
//import tz.tante.rent.manager.models.dtos.Responses.LandLords.LandLordWithTenants;
//import tz.tante.rent.manager.models.dtos.Responses.Tenants.TenantResponseDto;
//import tz.tante.rent.manager.services.LandLordService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.net.URI;
//
//@RequestMapping("/api/v1/landlords")
//@RestController
//@CrossOrigin(origins = "*")
//public class LandLordController {
//    private final LandLordService landLordService;
//
//    public LandLordController(LandLordService landLordService) {
//        this.landLordService = landLordService;
//    }
//
//    @PostMapping
//    public ResponseEntity<LandLordResponseDto> registerLandLord(@RequestBody LandLordRequestDto requestDto)
//    {
//        LandLordResponseDto createdLandLord = landLordService.registerLandLord(requestDto);
//        URI location = URI.create("landlords/"+createdLandLord.id());
//        return ResponseEntity.created(location).body(createdLandLord);
//    }
//    @PostMapping("/{landLordId}/tenant")
//    public ResponseEntity<TenantResponseDto> registerTenant(@PathVariable Long landLordId, @RequestBody TenantRequestDto requestDto)
//    {
//        TenantResponseDto responseDto = landLordService.registerTenant(landLordId,requestDto);
//        URI location = URI.create("tenant/"+responseDto.id());
//        return ResponseEntity.created(location).body(responseDto);
//    }
//
//    @GetMapping("{landLordId}/tenant")
//    public ResponseEntity<LandLordWithTenants> getTenants(@PathVariable Long landLordId)
//    {
//        LandLordWithTenants landlord = landLordService.getLandlordWithTenants(landLordId);
//        return ResponseEntity.ok(landlord);
//    }
//}
