package br.ifsp.demo.security.auth;

import br.ifsp.demo.domain.Cpf;
import br.ifsp.demo.security.user.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record RegisterUserRequest(
        @Schema(description = "Name", example = "John")
        String name,
        @Schema(description = "Lastname", example = "Snow")
        String lastname,
        @Schema(description = "Email to be used as login", example = "know.nothing@snow.com")
        String email,
        @Schema(description = "Password", example = "n3243#kFdj$")
        String password,
        @Schema(description = "Role", example = "PASSENGER")
        Role role,
        @Schema(description = "Cpf", example = "111.222.333-45")
        Cpf cpf,
        @Schema(description = "Birth Date", example = "2005-05-26")
        LocalDate birthDate
) {}
