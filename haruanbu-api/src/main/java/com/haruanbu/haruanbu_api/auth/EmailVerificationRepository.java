package com.haruanbu.haruanbu_api.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, UUID> {
    Optional<EmailVerification> findTopByEmailOrderByDateCreatedDesc(String email);

}
