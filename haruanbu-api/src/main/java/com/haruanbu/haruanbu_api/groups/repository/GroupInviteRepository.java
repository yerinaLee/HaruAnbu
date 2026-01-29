package com.haruanbu.haruanbu_api.groups.repository;

import com.haruanbu.haruanbu_api.groups.domain.GroupInvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GroupInviteRepository extends JpaRepository<GroupInvite, UUID> {
    Optional<GroupInvite> findByCode(String code);
    boolean existsByCode(String code);
}
