package com.taskmanager.service;

import com.taskmanager.model.Task;
import com.taskmanager.model.TaskHistory;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.TaskHistoryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TaskService {

    @Inject
    TaskRepository taskRepository;

    @Inject
    TaskHistoryRepository taskHistoryRepository;

    @Transactional
    public Task create(Task task) {
        // Validar e definir valores padrão
        if (task.status == null || task.status.isEmpty()) {
            task.status = "EM_ANDAMENTO";
        }

        validateCategory(task.category);
        validatePriority(task.priority);
        validateStatus(task.status);

        // Persistir tarefa
        taskRepository.persist(task);

        // Registrar no histórico
        TaskHistory history = new TaskHistory();
        history.taskId = task.id;
        history.action = "CRIADA";
        history.oldStatus = null;
        history.newStatus = task.status;
        history.description = "Tarefa criada: " + task.title;
        taskHistoryRepository.persist(history);

        return task;
    }

    public Optional<Task> findById(Long id) {
        return taskRepository.findByIdOptional(id);
    }

    public List<Task> findByUserId(Long userId) {
        return taskRepository.findByUserId(userId);
    }

    public List<Task> findByUserIdAndStatus(Long userId, String status) {
        validateStatus(status);
        return taskRepository.findByUserIdAndStatus(userId, status);
    }

    public List<Task> findAll() {
        return taskRepository.findAllOrdered();
    }

    @Transactional
    public Task update(Long id, Task updatedTask) {
        Task task = taskRepository.findByIdOptional(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada"));

        String oldStatus = task.status;

        // Validar dados
        validateCategory(updatedTask.category);
        validatePriority(updatedTask.priority);
        validateStatus(updatedTask.status);

        // Atualizar campos
        task.title = updatedTask.title;
        task.description = updatedTask.description;
        task.category = updatedTask.category;
        task.priority = updatedTask.priority;
        task.status = updatedTask.status;
        task.dueDate = updatedTask.dueDate;

        // Se status mudou para CONCLUIDO, registrar data de conclusão
        if ("CONCLUIDO".equals(updatedTask.status) && !"CONCLUIDO".equals(oldStatus)) {
            task.completedAt = LocalDateTime.now();
        }

        // Registrar no histórico se status mudou
        if (!oldStatus.equals(updatedTask.status)) {
            TaskHistory history = new TaskHistory();
            history.taskId = task.id;
            history.action = "STATUS_ALTERADO";
            history.oldStatus = oldStatus;
            history.newStatus = updatedTask.status;
            history.description = "Status alterado de " + oldStatus + " para " + updatedTask.status;
            taskHistoryRepository.persist(history);
        }

        return task;
    }

    @Transactional
    public Task markAsCompleted(Long id) {
        Task task = taskRepository.findByIdOptional(id)
                .orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada"));

        String oldStatus = task.status;
        task.status = "CONCLUIDO";
        task.completedAt = LocalDateTime.now();

        // Registrar no histórico
        TaskHistory history = new TaskHistory();
        history.taskId = task.id;
        history.action = "CONCLUIDA";
        history.oldStatus = oldStatus;
        history.newStatus = "CONCLUIDO";
        history.description = "Tarefa marcada como concluída: " + task.title;
        taskHistoryRepository.persist(history);

        return task;
    }

    @Transactional
    public boolean delete(Long id) {
        Optional<Task> task = taskRepository.findByIdOptional(id);
        if (task.isPresent()) {
            // Registrar no histórico antes de deletar
            TaskHistory history = new TaskHistory();
            history.taskId = task.get().id;
            history.action = "DELETADA";
            history.oldStatus = task.get().status;
            history.newStatus = null;
            history.description = "Tarefa deletada: " + task.get().title;
            taskHistoryRepository.persist(history);
        }

        return taskRepository.deleteById(id);
    }

    public List<TaskHistory> getHistory(Long taskId) {
        return taskHistoryRepository.findByTaskId(taskId);
    }

    private void validateCategory(String category) {
        List<String> validCategories = List.of(
                "TECNOLOGIA", "CERTIFICACAO", "TRABALHO", "PESSOAL", "SAUDE", "EDUCACAO", "OUTRO"
        );

        if (!validCategories.contains(category.toUpperCase())) {
            throw new IllegalArgumentException("Categoria inválida. Valores aceitos: " + validCategories);
        }
    }

    private void validatePriority(String priority) {
        List<String> validPriorities = List.of("BAIXA", "MEDIA", "ALTA", "URGENTE");

        if (!validPriorities.contains(priority.toUpperCase())) {
            throw new IllegalArgumentException("Prioridade inválida. Valores aceitos: " + validPriorities);
        }
    }

    private void validateStatus(String status) {
        List<String> validStatuses = List.of("EM_ANDAMENTO", "CONCLUIDO");

        if (!validStatuses.contains(status.toUpperCase())) {
            throw new IllegalArgumentException("Status inválido. Valores aceitos: " + validStatuses);
        }
    }
}