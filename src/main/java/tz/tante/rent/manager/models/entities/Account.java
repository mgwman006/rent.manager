package tz.tante.rent.manager.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "accounts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Account extends BaseEntity
{
  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false, unique = true)
  private String phoneNumber;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private boolean enabled = true;

  @Column(nullable = false)
  private boolean emailVerified = false;

  @Column(nullable = false)
  private boolean phoneNumberVerified = false;

  @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
  private User user;

  public Account(String phoneNumber,String email, String password)
  {
    this.phoneNumber = phoneNumber;
    this.password = password;
    this.email = email;
  }
}
