package com.tante.landlordtenant.repository;

import com.tante.landlordtenant.models.Entities.Landlord.Landlord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LandlordRepository extends JpaRepository<Landlord,Long>
{
}
