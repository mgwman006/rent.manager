-- Create notifications table for the generic notification dispatch system
-- This table stores pending, sent, and failed notifications across all contexts
-- (lease invitations, viewing bookings, etc.)

CREATE TABLE IF NOT EXISTS notifications
(
  id                    BIGINT                                       NOT NULL AUTO_INCREMENT,
  version               BIGINT                                       DEFAULT NULL,
  created_at            DATETIME(6)                                  DEFAULT NULL,
  updated_at            DATETIME(6)                                  DEFAULT NULL,
  title                 VARCHAR(255)                                NOT NULL,
  message               TEXT                                         NOT NULL,
  type                  ENUM ('INVALID', 'SMS', 'EMAIL')             NOT NULL,
  recipient_phone_number VARCHAR(255)                                 DEFAULT NULL,
  recipient_email       VARCHAR(255)                                 DEFAULT NULL,
  reference_type        VARCHAR(255)                                 DEFAULT NULL,
  reference_id          VARCHAR(255)                                 DEFAULT NULL,
  status                ENUM ('PENDING', 'SENT', 'FAILED')          NOT NULL DEFAULT 'PENDING',
  attempts              INT                                          NOT NULL DEFAULT 0,
  provider_reference    VARCHAR(255)                                 DEFAULT NULL,
  last_error            TEXT                                         DEFAULT NULL,
  next_retry_at         DATETIME(6)                                  DEFAULT NULL,
  sent_at               DATETIME(6)                                  DEFAULT NULL,
  PRIMARY KEY (id),
  INDEX idx_notifications_status (status),
  INDEX idx_notifications_reference (reference_type, reference_id),
  INDEX idx_notifications_next_retry (next_retry_at),
  INDEX idx_notifications_phone (recipient_phone_number),
  INDEX idx_notifications_email (recipient_email)
) ENGINE = InnoDB;
