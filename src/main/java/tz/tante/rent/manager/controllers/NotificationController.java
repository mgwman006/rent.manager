package tz.tante.rent.manager.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tz.tante.rent.manager.messaging.models.NotificationDTO;
import tz.tante.rent.manager.messaging.notification.NotificationService;

@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
public class NotificationController
{
  private final NotificationService notificationService;

  @PostMapping
  public ResponseEntity<Void> createNotification(@RequestBody NotificationDTO notificationDTO)
  {
    notificationService.createPendingNotification(notificationDTO);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}

