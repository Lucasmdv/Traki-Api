package com.traki.trakiapi.models.entities;

import com.traki.trakiapi.security.model.CredentialsEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Table(name = "Users")
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

    @Column(name = "user_first_name", nullable = false, length = 20)
    private String firstName;

    @Column(name = "user_last_name", nullable = false, length = 20)
    private String lastName;

    @Column(name = "user_date_of_registration")
    @CreationTimestamp
    private LocalDate dateOfRegistration;

    @OneToOne(mappedBy = "user")
    private CredentialsEntity credentials;
}
