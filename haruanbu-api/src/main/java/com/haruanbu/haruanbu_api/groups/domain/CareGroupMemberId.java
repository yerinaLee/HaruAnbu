package com.haruanbu.haruanbu_api.groups.domain;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class CareGroupMemberId implements Serializable {
    private UUID groupId;
    private UUID userId;

    protected CareGroupMemberId() {}

    public CareGroupMemberId(UUID groupId, UUID userId) {
        this.groupId = groupId;
        this.userId = userId;
    }

    public UUID getGroupId() { return groupId; }
    public UUID getUserId() { return userId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CareGroupMemberId that)) return false;
        return Objects.equals(groupId, that.groupId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, userId);
    }
}
