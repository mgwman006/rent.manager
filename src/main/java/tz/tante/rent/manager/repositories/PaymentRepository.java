package tz.tante.rent.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.tante.rent.manager.models.entities.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>
{
}
