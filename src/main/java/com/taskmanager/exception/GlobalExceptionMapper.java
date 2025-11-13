package com.taskmanager.exception;

import com.taskmanager.dto.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {

        // Tratamento de exceções de argumento ilegal
        if (exception instanceof IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Bad Request", exception.getMessage(), 400))
                    .build();
        }

        // Tratamento de exceções de não encontrado
        if (exception instanceof jakarta.ws.rs.NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Not Found", "Recurso não encontrado", 404))
                    .build();
        }

        // Tratamento genérico de exceções
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Internal Server Error",
                        "Ocorreu um erro inesperado no servidor", 500))
                .build();
    }
}