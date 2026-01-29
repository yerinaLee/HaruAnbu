package com.haruanbu.haruanbu_api.groups.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "care_group_members")
public class CareGroupMember {

    @EmbeddedId
    private CareGroupMemberId id;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role", nullable = false)
    private GroupMemberRole memberRole;

    // jsonb 컬럼. 외부 라이브러리 없이 String으로만 처리(초기엔 이게 제일 안전)
    @Column(nullable = false)
    private String permissions;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected CareGroupMember() {}

    public CareGroupMember(CareGroupMemberId id, GroupMemberRole memberRole, String permissions, OffsetDateTime createdAt) {
        this.id = id;
        this.memberRole = memberRole;
        this.permissions = permissions == null ? "{}" : permissions;
        this.createdAt = createdAt;
    }

    public CareGroupMemberId getId() { return id; }
    public GroupMemberRole getMemberRole() { return memberRole; }
}
