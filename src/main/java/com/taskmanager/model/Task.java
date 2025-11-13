package com.taskmanager.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task extends PanacheEntity {

    @Column(name = "user_id", nullable = false)
    public Long userId;

    @Column(nullable = false, length = 255)
    public String title;

    @Column(columnDefinition = "CLOB")
    public String description;

    @Column(nullable = false, length = 50)
    public String category;

    @Column(nullable = false, length = 50)
    public String priority;

    @Column(nullable = false, length = 50)
    public String status = "EM_ANDAMENTO";

    @Column(name = "due_date")
    public LocalDateTime dueDate;

    @Column(name = "completed_at")
    public LocalDateTime completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}