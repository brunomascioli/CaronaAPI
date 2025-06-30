package br.ifsp.demo.integration.ui.util;

import com.github.javafaker.Faker;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class FakeDataFactory {

    private static final Faker faker = new Faker(Locale.forLanguageTag("pt-BR"));

    private static final List<String> cpfList = List.of(
        "529.982.247-25", "390.533.447-05", "215.365.987-43",
        "746.821.360-20", "123.456.789-09", "654.321.987-00",
        "321.654.987-66", "222.333.444-55", "111.222.333-96", "147.258.369-00"
    );
    private static final AtomicInteger cpfIndex = new AtomicInteger(0);

    // Pessoa
    public static String randomName() {
        return faker.name().firstName();
    }

    public static String randomLastName() {
        return faker.name().lastName();
    }

    public static String randomEmail() {
        return faker.internet().emailAddress("testuser");
    }

    public static String randomBirthDate() {
        return "1990-01-01";
    }

    public static String strongPassword() {
        return "SenhaForte123!";
    }

    public static String generateValidCpfFromList() {
        int index = cpfIndex.getAndIncrement() % cpfList.size();
        return cpfList.get(index);
    }

    public static String randomPhoneNumber() {
        return faker.phoneNumber().cellPhone();
    }

    // Carro
    public static String randomCarBrand() {
        return faker.company().name();
    }

    public static String randomCarModel() {
        return faker.bothify("Model-###");
    }

    public static String randomColor() {
        return faker.color().name();
    }

    public static int randomSeats() {
        return faker.number().numberBetween(2, 7);
    }

    public static String randomPlate() {
        // Gera placa no formato ABC-1234
        String letters = faker.letterify("???").toUpperCase();
        String numbers = faker.numerify("####");
        return letters + "-" + numbers;
    }

    // Endereço
    public static String randomAddress() {
        return faker.address().streetName() + ", " +
            faker.number().numberBetween(10, 999) + ", " +
            faker.address().streetSuffix() + ", " +
            faker.address().city();
    }
}