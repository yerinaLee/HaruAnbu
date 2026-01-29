package com.haruanbu.haruanbu_api.users.repository;

import com.haruanbu.haruanbu_api.users.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
