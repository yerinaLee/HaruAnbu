package com.haruanbu.haruanbu_api.users.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    private String email;
    private String phone;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected User() {}

    public UUID getId() {return id;}
    public UserRole getRole() {return role;}
}
