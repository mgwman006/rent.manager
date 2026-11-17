package tz.tante.rent.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tz.tante.rent.manager.messaging.notification.enums.NotificationStatus;
import tz.tante.rent.manager.messaging.notification.entities.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long>
{
  List<Notification> findByStatusOrderByCreatedAtAsc(NotificationStatus status);
}
