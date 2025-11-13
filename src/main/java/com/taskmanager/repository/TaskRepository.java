package com.taskmanager.repository;

import com.taskmanager.model.Task;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class TaskRepository implements PanacheRepository<Task> {

    public List<Task> findByUserId(Long userId) {
        return find("userId", userId).list();
    }

    public List<Task> findByUserIdAndStatus(Long userId, String status) {
        return find("userId = ?1 and status = ?2", userId, status).list();
    }

    public List<Task> findAllOrdered() {
        return find("order by createdAt desc").list();
    }
}