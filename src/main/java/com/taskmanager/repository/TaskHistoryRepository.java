package com.taskmanager.repository;

import com.taskmanager.model.TaskHistory;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class TaskHistoryRepository implements PanacheRepository<TaskHistory> {

    public List<TaskHistory> findByTaskId(Long taskId) {
        return find("taskId", taskId).list();
    }
}