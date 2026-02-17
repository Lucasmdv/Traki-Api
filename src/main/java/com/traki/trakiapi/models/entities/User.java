package com.traki.trakiapi.models.entities;

import com.traki.trakiapi.security.model.CredentialsEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.Set;

@Entity
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id",nullable = false, unique = true)
    public long Id;

    @Column(nullable = false, unique = true)
    public String UserName;

    public String Password;

    public String Role;

    @Column(name = "user_first_name", nullable = false, length = 20)
    private String firstName;

    @Column(name = "user_last_name", nullable = false, length = 20)
    private String lastName;

    @Column(name = "user_dni", nullable = false, length = 8, unique = true)
    private String dni;

    @Column(name = "user_date_of_registration")
    @CreationTimestamp
    private LocalDate dateOfRegistration;

    @OneToOne(mappedBy = "user")
    private CredentialsEntity credentials;
}
