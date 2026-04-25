package tz.tante.rent.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tz.tante.rent.manager.models.entities.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long>
{
}
