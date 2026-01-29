package com.haruanbu.haruanbu_api.groups.repository;

import com.haruanbu.haruanbu_api.groups.domain.CareGroupMember;
import com.haruanbu.haruanbu_api.groups.domain.CareGroupMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CareGroupMemberRepository extends JpaRepository<CareGroupMember, CareGroupMemberId> {
    List<CareGroupMember> findByIdGroupId(UUID groupId);
    Optional<CareGroupMember> findByIdGroupIdAndIdUserId(UUID groupId, UUID userId);
    boolean existsByIdGroupIdAndIdUserId(UUID groupId, UUID userId);
}
