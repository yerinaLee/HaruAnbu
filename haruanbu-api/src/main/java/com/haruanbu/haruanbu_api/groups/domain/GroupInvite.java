package com.haruanbu.haruanbu_api.groups.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "group_invites")
public class GroupInvite {

    @Id
    private UUID id;

    @Column(name = "group_id", nullable = false)
    private UUID groupId;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(nullable = false, unique = true, length = 32)
    private String code;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @Column(name = "used_by")
    private UUID usedBy;

    @Column(name = "used_at")
    private OffsetDateTime usedAt;

    protected GroupInvite() {}

    public GroupInvite(UUID id, UUID groupId, UUID createdBy, String code, OffsetDateTime expiresAt) {
        this.id = id;
        this.groupId = groupId;
        this.createdBy = createdBy;
        this.code = code;
        this.expiresAt = expiresAt;
    }

    public UUID getId() { return id; }
    public UUID getGroupId() { return groupId; }
    public String getCode() { return code; }
    public OffsetDateTime getExpiresAt() { return expiresAt; }
    public UUID getUsedBy() { return usedBy; }
    public OffsetDateTime getUsedAt() { return usedAt; }

    public boolean isUsed() {
        return usedAt != null || usedBy != null;
    }

    public void markUsed(UUID userId, OffsetDateTime now) {
        this.usedBy = userId;
        this.usedAt = now;
    }
}
