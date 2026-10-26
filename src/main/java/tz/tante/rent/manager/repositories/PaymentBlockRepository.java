package tz.tante.rent.manager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tz.tante.rent.manager.models.entities.PaymentBlock;

@Repository
public interface PaymentBlockRepository extends JpaRepository<PaymentBlock, Long>
{
}
