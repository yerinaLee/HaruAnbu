package com.haruanbu.haruanbu_api.auth;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="email_verification")
public class EmailVerification {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(name = "verification_code", nullable = false, length = 255)
    private String verificationCode;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount ;

    @Column(name="date_created", nullable = false)
    private Instant dateCreated ;

    protected EmailVerification(){}

    public EmailVerification(String email, String verificationCode, Instant expiresAt) {
        this.email = email;
        this.verificationCode = verificationCode;
        this.expiresAt = expiresAt;
        this.attemptCount = 0;
        this.dateCreated = Instant.now();
    }

    public String getEmail() { return email; }
    public Instant getDateCreated() { return dateCreated; }


}
