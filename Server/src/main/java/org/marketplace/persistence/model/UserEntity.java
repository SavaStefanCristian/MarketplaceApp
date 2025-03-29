package org.marketplace.persistence.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@Entity
@Table(name = "\"UserEntity\"", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "unique_username", columnNames = {"username"})
})
public class UserEntity implements PersistableEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Lob
    @Column(name = "username")
    private String username;

    @Lob
    @Column(name = "\"passHash\"")
    private String passHash;

    @Lob
    @Column(name = "phone")
    private String phone;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
          name="\"UserRole\"",
         joinColumns = @JoinColumn(
                 name="\"userId\"", referencedColumnName = "id"),
         inverseJoinColumns = @JoinColumn(
                 name = "\"roleId\"", referencedColumnName = "id"))
    private Collection<RoleEntity> roles;

}