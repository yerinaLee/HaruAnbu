package com.haruanbu.haruanbu_api.groups.repository;

import com.haruanbu.haruanbu_api.groups.domain.CareGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CareGroupRepository extends JpaRepository<CareGroup, UUID> {
}
