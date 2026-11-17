package tz.tante.rent.manager.messaging.notification.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.tante.rent.manager.messaging.notification.enums.NotificationStatus;
import tz.tante.rent.manager.messaging.notification.enums.NotificationType;
import tz.tante.rent.manager.models.entities.BaseEntity;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
public class Notification extends BaseEntity
{
  @Column(nullable = false)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String message;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private NotificationType type;

  @Column
  private String recipientPhoneNumber;

  @Column
  private String recipientEmail;

  @Column
  private String referenceType;

  @Column
  private String referenceId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private NotificationStatus status = NotificationStatus.PENDING;

  @Column(nullable = false)
  private int attempts = 0;

  @Column
  private String providerReference;

  @Column(columnDefinition = "TEXT")
  private String lastError;

  @Column
  private java.time.LocalDateTime nextRetryAt;

  @Column
  private java.time.LocalDateTime sentAt;
}
