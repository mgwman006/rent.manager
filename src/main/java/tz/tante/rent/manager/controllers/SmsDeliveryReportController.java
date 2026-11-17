package tz.tante.rent.manager.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1/sms")
@CrossOrigin(origins = "*", methods = {RequestMethod.POST}, allowedHeaders = "*")
public class SmsDeliveryReportController
{
  @PostMapping(
    value = "/delivery-report",
    consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
    produces = MediaType.TEXT_PLAIN_VALUE
  )
  public ResponseEntity<String> handleDeliveryReport(@RequestParam Map<String, String> payload)
  {
    System.out.println("SMS delivery report callback received: " + payload);
    return ResponseEntity.ok("OK");
  }
}
