package br.ifsp.demo.integration.api.utils;

import br.ifsp.demo.security.user.Role;

import java.time.LocalDate;

public record RegisterUserRequest(
        String name,
        String lastname,
        String email,
        String password,
        Role role,
        String cpf, // <--- This should be a String for your test client
        LocalDate birthDate
) {}
