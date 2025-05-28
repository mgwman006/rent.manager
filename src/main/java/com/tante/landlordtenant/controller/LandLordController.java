package com.tante.landlordtenant.controller;

import com.tante.landlordtenant.models.Requests.LandLords.LandLordRequestDto;
import com.tante.landlordtenant.models.Responses.LandLords.LandLordResponseDto;
import com.tante.landlordtenant.models.Responses.Users.UserResponseDto;
import com.tante.landlordtenant.service.LandLordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequestMapping("/api/v1/landlords")
@RestController
@CrossOrigin(origins = "*")
public class LandLordController {
    private final LandLordService landLordService;

    public LandLordController(LandLordService landLordService) {
        this.landLordService = landLordService;
    }

    @PostMapping
    public ResponseEntity<LandLordResponseDto> registerLandLord(@RequestBody LandLordRequestDto requestDto)
    {
        LandLordResponseDto createdLandLord = landLordService.registerLandLord(requestDto);
        URI location = URI.create("landlords/"+createdLandLord.id());
        return ResponseEntity.created(location).body(createdLandLord);
    }
}
