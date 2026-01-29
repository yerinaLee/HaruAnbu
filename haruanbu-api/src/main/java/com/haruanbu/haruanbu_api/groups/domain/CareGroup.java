package com.haruanbu.haruanbu_api.groups.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "care_groups")
public class CareGroup {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected CareGroup() {}

    public CareGroup(UUID id, String name, UUID createdBy, OffsetDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
}
