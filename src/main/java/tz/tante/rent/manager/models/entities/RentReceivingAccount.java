package tz.tante.rent.manager.models.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.tante.rent.manager.enums.MobileMoneyProvider;
import tz.tante.rent.manager.enums.PaymentMethod;

@Entity
@Table(name = "rent_receiving_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentReceivingAccount extends BaseEntity
{
  @ManyToOne
  @JoinColumn(name = "rental_profile_id")
  RentalProfile rentalProfile;

  @Enumerated(EnumType.STRING)
  PaymentMethod paymentMethod;

  private String accountNumber;

  private String bankName;

  @Enumerated(EnumType.STRING)
  private MobileMoneyProvider mobileMoneyProvider;

  private String mobileMoneyNumber;

  private boolean active = true;

  private boolean isDefault = false;

  @Column(length = 1000)
  private String paymentInstruction;

}
