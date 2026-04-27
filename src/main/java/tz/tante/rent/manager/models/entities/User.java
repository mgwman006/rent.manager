package tz.tante.rent.manager.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseEntity
{
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private boolean enabled = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
      name = "user_roles",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "user")
    private Landlord landlordProfile;

    @OneToOne(mappedBy = "user")
    private Tenant tenantProfile;

    public User(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    public void addRole(Role role)
    {
        this.roles.add(role);
    }
}