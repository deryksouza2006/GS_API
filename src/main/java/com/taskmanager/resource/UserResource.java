package com.taskmanager.resource;

import com.taskmanager.dto.AuthResponse;
import com.taskmanager.dto.ErrorResponse;
import com.taskmanager.dto.LoginRequest;
import com.taskmanager.model.User;
import com.taskmanager.service.UserService;
import com.taskmanager.service.AuthService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;

    @Inject
    AuthService authService;

    @POST
    @Path("/register")
    public Response register( User user) {
        try {
            User createdUser = userService.create(user);

            // Não retornar a senha
            createdUser.password = null;


            return Response.status(Response.Status.CREATED)
                    .entity(new AuthResponse(createdUser, "Usuário registrado com sucesso"))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Bad Request", e.getMessage(), 400))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Internal Server Error", "Erro ao registrar usuário", 500))
                    .build();
        }
    }

    @POST
    @Path("/login")
    public Response login( LoginRequest loginRequest) {
        try {
            Optional<User> user = authService.authenticate(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            if (user.isPresent()) {
                User authenticatedUser = user.get();
                authenticatedUser.password = null;

                return Response.ok()
                        .entity(new AuthResponse(authenticatedUser, "Login realizado com sucesso"))
                        .build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorResponse("Unauthorized", "Credenciais inválidas", 401))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Internal Server Error", "Erro ao autenticar usuário", 500))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        try {
            Optional<User> user = userService.findById(id);

            if (user.isPresent()) {
                User foundUser = user.get();
                foundUser.password = null;
                return Response.ok(foundUser).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Not Found", "Usuário não encontrado", 404))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Internal Server Error", "Erro ao buscar usuário", 500))
                    .build();
        }
    }

    @GET
    public Response findAll() {
        try {
            List<User> users = userService.findAll();

            // Remover senhas da resposta
            users.forEach(user -> user.password = null);

            return Response.ok(users).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Internal Server Error", "Erro ao listar usuários", 500))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, User user) {
        try {
            User updatedUser = userService.update(id, user);
            updatedUser.password = null;

            return Response.ok(updatedUser).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Bad Request", e.getMessage(), 400))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Internal Server Error", "Erro ao atualizar usuário", 500))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            boolean deleted = userService.delete(id);

            if (deleted) {
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse("Not Found", "Usuário não encontrado", 404))
                        .build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Bad Request", e.getMessage(), 400))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Internal Server Error", "Erro ao deletar usuário", 500))
                    .build();
        }
    }
}