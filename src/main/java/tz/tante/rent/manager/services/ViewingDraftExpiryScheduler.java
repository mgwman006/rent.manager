package tz.tante.rent.manager.services;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ViewingDraftExpiryScheduler {
  private static final Logger LOGGER = LoggerFactory.getLogger(ViewingDraftExpiryScheduler.class);

  private final ViewingService viewingService;

  @Scheduled(fixedDelayString = "${viewing.booking.hold-scan-ms:60000}")
  public void expireStaleDrafts()
  {
    int expired = viewingService.expireStaleDrafts();
    if (expired > 0)
    {
      LOGGER.info("Expired {} stale draft viewing bookings.", expired);
    }
  }
}
