package tz.tante.rent.manager.messaging.models;

public record RentPaymentReceived(
  String eventId,
  Long leaseId,
  String amount
) {}