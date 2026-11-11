-- Viewing booking schema. The service runs with ddl-auto: validate, so these
-- definitions must stay in sync with the JPA entities.

CREATE TABLE IF NOT EXISTS viewing_bookings
(
  id                        BIGINT                                                                                        NOT NULL AUTO_INCREMENT,
  version                   BIGINT                                                                                        DEFAULT NULL,
  created_at                DATETIME(6)                                                                                   DEFAULT NULL,
  updated_at                DATETIME(6)                                                                                   DEFAULT NULL,
  reference                 VARCHAR(60)                                                                                   NOT NULL,
  rental_profile_id         BIGINT                                                                                        NOT NULL,
  property_id               BIGINT                                                                                        NOT NULL,
  start_time                DATETIME(6)                                                                                   NOT NULL,
  end_time                  DATETIME(6)                                                                                   NOT NULL,
  requester_user_account_id BIGINT                                                                                        NOT NULL,
  status                    ENUM ('CANCELLED','CLOSED','CONFIRMED','DRAFT','EXPIRED','PENDING','REJECTED','RESCHEDULED')  NOT NULL,
  service_fee               DECIMAL(38, 2)                                                                                NOT NULL,
  payment_status            ENUM ('AWAITING_PAYMENT','FAILED','NOT_REQUIRED','PAID','REFUNDED')                           NOT NULL,
  payment_updated_at        DATETIME(6)                                                                                   DEFAULT NULL,
  hold_expires_at           DATETIME(6)                                                                                   DEFAULT NULL,
  notes                     VARCHAR(500)                                                                                  DEFAULT NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_viewing_bookings_reference UNIQUE (reference),
  INDEX idx_viewing_bookings_rental_profile (rental_profile_id),
  INDEX idx_viewing_bookings_property (property_id),
  INDEX idx_viewing_bookings_requester (requester_user_account_id),
  INDEX idx_viewing_bookings_start_time (start_time),
  CONSTRAINT fk_viewing_bookings_rental_profile
    FOREIGN KEY (rental_profile_id) REFERENCES rental_profiles (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS viewing_outcomes
(
  id                 BIGINT                                                                                  NOT NULL AUTO_INCREMENT,
  version            BIGINT                                                                                  DEFAULT NULL,
  created_at         DATETIME(6)                                                                             DEFAULT NULL,
  updated_at         DATETIME(6)                                                                             DEFAULT NULL,
  viewing_booking_id BIGINT                                                                                  NOT NULL,
  type               ENUM ('AGENT_ABSENT','COMPLETED','RESCHEDULED','TENANT_ABSENT','UNIT_NOT_AVAILABLE')    NOT NULL,
  notes              VARCHAR(500)                                                                            DEFAULT NULL,
  recorded_at        DATETIME(6)                                                                             NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_viewing_outcomes_booking UNIQUE (viewing_booking_id),
  CONSTRAINT fk_viewing_outcomes_booking
    FOREIGN KEY (viewing_booking_id) REFERENCES viewing_bookings (id)
) ENGINE = InnoDB;