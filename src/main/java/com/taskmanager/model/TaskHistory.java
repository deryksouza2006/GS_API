package com.taskmanager.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_history")
public class TaskHistory extends PanacheEntity {

    @Column(name = "task_id", nullable = false)
    public Long taskId;

    @Column(nullable = false, length = 50)
    public String action;

    @Column(name = "old_status", length = 50)
    public String oldStatus;

    @Column(name = "new_status", length = 50)
    public String newStatus;

    @Column(columnDefinition = "CLOB")
    public String description;

    @Column(name = "changed_at", nullable = false, updatable = false)
    public LocalDateTime changedAt;

    @PrePersist
    protected void onCreate() {
        changedAt = LocalDateTime.now();
    }
}