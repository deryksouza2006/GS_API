package com.taskmanager.resource;

import com.taskmanager.dto.ErrorResponse;
import com.taskmanager.model.Task;
import com.taskmanager.model.TaskHistory;
import com.taskmanager.service.TaskService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Path("/api/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TaskResource {

    @Inject
    TaskService taskService;

    @POST
    public Response create(Task task) {
        try {
            Task createdTask = taskService.create(task);
            return Response.status(Response.Status.CREATED).entity(createdTask).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Bad Request", e.getMessage(), 400))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Internal Server Error", "Erro ao criar tarefa", 500))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        try {
            Optional<Task> task = taskService.findById(id);

            if (task.isPresent()) {
                return Response.ok(task.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Not Found", "Tarefa não encontrada", 404))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Internal Server Error", "Erro ao buscar tarefa", 500))
                    .build();
        }
    }

    @GET
    @Path("/user/{userId}")
    public Response findByUserId(@PathParam("userId") Long userId) {
        try {
            List<Task> tasks = taskService.findByUserId(userId);
            return Response.ok(tasks).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Internal Server Error", "Erro ao listar tarefas", 500))
                    .build();
        }
    }

    @GET
    @Path("/user/{userId}/status/{status}")
    public Response findByUserIdAndStatus(
            @PathParam("userId") Long userId,
            @PathParam("status") String status) {
        try {
            List<Task> tasks = taskService.findByUserIdAndStatus(userId, status);
            return Response.ok(tasks).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Bad Request", e.getMessage(), 400))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Internal Server Error", "Erro ao listar tarefas", 500))
                    .build();
        }
    }

    @GET
    public Response findAll() {
        try {
            List<Task> tasks = taskService.findAll();
            return Response.ok(tasks).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Internal Server Error", "Erro ao listar tarefas", 500))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, Task task) {
        try {
            Task updatedTask = taskService.update(id, task);
            return Response.ok(updatedTask).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Bad Request", e.getMessage(), 400))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Internal Server Error", "Erro ao atualizar tarefa", 500))
                    .build();
        }
    }

    @PATCH
    @Path("/{id}/complete")
    public Response markAsCompleted(@PathParam("id") Long id) {
        try {
            Task updatedTask = taskService.markAsCompleted(id);
            return Response.ok(updatedTask).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Bad Request", e.getMessage(), 400))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Internal Server Error", "Erro ao marcar tarefa como concluída", 500))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            boolean deleted = taskService.delete(id);

            if (deleted) {
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Not Found", "Tarefa não encontrada", 404))
                        .build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Bad Request", e.getMessage(), 400))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Internal Server Error", "Erro ao deletar tarefa", 500))
                    .build();
        }
    }

    @GET
    @Path("/{id}/history")
    public Response getHistory(@PathParam("id") Long id) {
        try {
            List<TaskHistory> history = taskService.getHistory(id);
            return Response.ok(history).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Internal Server Error", "Erro ao buscar histórico", 500))
                    .build();
        }
    }
}