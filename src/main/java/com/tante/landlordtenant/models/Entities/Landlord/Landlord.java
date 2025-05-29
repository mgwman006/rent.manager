package com.tante.landlordtenant.models.Entities.Landlord;

import com.tante.landlordtenant.models.Entities.Contract;
import com.tante.landlordtenant.models.Entities.House;
import com.tante.landlordtenant.models.Entities.PaymentDetails;
import com.tante.landlordtenant.models.Entities.Tenants.Tenant;
import com.tante.landlordtenant.models.Entities.Users.User;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "landlords")
public class Landlord
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String phoneNumber;
    @Column(unique = true)
    private String email;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "landlord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tenant> tenants;

    public Landlord() {
    }

    public Landlord(String firstName, String lastName, String phoneNumber, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Tenant> getTenants() {
        return tenants;
    }

    public void setTenants(List<Tenant> tenants) {
        this.tenants = tenants;
    }
    public void addTenant(Tenant tenant)
    {
        this.tenants.add(tenant);
    }
}
