package br.ifsp.demo.integration.api.utils;

import br.ifsp.demo.domain.Cpf;
import br.ifsp.demo.security.user.Role;
import br.ifsp.demo.security.user.User;
import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class DriverEntityBuilder {

    private static Faker faker = new Faker();
    private static List<String> cpfs = List.of(
            "679.538.950-21",
            "733.677.500-42",
            "804.530.890-70",
            "241.811.950-58",
            "877.321.140-00",
            "781.669.170-09",
            "502.979.848-02"
    );

    public static RegisterUserRequest createRandomDriverUser(String password){
        return new RegisterUserRequest(
                faker.name().firstName(),
                faker.name().lastName(),
                faker.internet().emailAddress(),
                password,
                Role.DRIVER,
                getRandomCPF(),
                LocalDate.ofInstant(faker.date().birthday(20, 80).toInstant(), ZoneId.systemDefault())
        );
    }

    public static RegisterUserRequest createDriverByEmail(String password, String email){
        return new RegisterUserRequest(
                faker.name().firstName(),
                faker.name().lastName(),
                email,
                password,
                Role.DRIVER,
                getRandomCPF(),
                LocalDate.ofInstant(faker.date().birthday(20, 80).toInstant(), ZoneId.systemDefault())
        );
    }

    public static RegisterUserRequest createWithNullCPF(){
        return new RegisterUserRequest(
                faker.name().firstName(),
                faker.name().lastName(),
                faker.internet().emailAddress(),
                faker.internet().password(),
                Role.DRIVER,
                null,
                LocalDate.ofInstant(faker.date().birthday(20, 80).toInstant(), ZoneId.systemDefault())
        );
    }

    private static String getRandomCPF(){
        Random rand = new Random();
        return cpfs.get(rand.nextInt(cpfs.size()));
    }

}
