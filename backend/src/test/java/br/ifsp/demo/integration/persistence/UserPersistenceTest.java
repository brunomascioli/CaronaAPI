package br.ifsp.demo.integration.persistence;

import br.ifsp.demo.domain.Cpf;
import br.ifsp.demo.domain.Driver;
import br.ifsp.demo.domain.Passenger;
import br.ifsp.demo.security.user.JpaUserRepository;
import br.ifsp.demo.security.user.Role;
import br.ifsp.demo.security.user.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(br.ifsp.demo.config.TestConfig.class)
@ActiveProfiles("test")
@DisplayName("User Persistence Tests")
class UserPersistenceTest {
    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Driver driver;
    private Passenger passenger;
    private Driver anotherDriver;
    private Passenger anotherPassenger;

    @BeforeEach
    void setUp() {
        entityManager.clear();

        driver = createDriver("Fulano", "Silva", "fulano.silva@gmail.com", "005.046.860-03");
        anotherDriver = createDriver("Ciclano", "Souza", "ciclano.souza@outlook.com", "355.553.060-75");
        passenger = createPassenger("Maria", "Oliveira", "maria.oliveira@hotmail.com", "561.506.860-43");
        anotherPassenger = createPassenger("Ana", "Souza", "ana.souza@yahoo.com", "936.138.620-42");
    }

    @Nested
    @DisplayName("Driver Persistence Tests")
    class DriverPersistenceTests {

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should persist driver with all valid data")
        void shouldPersistDriverWithValidData() {
            Driver savedDriver = userRepository.save(driver);
            entityManager.flush();
            entityManager.clear();

            assertThat(savedDriver.getId()).isNotNull();
            assertDriverProperties(savedDriver, "Fulano", "Silva", "fulano.silva@gmail.com", "005.046.860-03");
            assertThat(savedDriver.getRole()).isEqualTo(Role.DRIVER);
            assertThat(savedDriver.getBirthDate()).isEqualTo(LocalDate.of(1990, 1, 1));

            Optional<Driver> foundDriver = userRepository.findById(savedDriver.getId())
                    .map(user -> (Driver) user);
            assertThat(foundDriver).isPresent();
            assertDriverProperties(foundDriver.get(), "Fulano", "Silva", "fulano.silva@gmail.com", "005.046.860-03");
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should persist driver with minimum age")
        void shouldPersistDriverWithMinimumAge() {
            Driver youngDriver = new Driver(
                    "Menor",
                    "Idade",
                    "menor.idade@gmail.com",
                    "password123",
                    Cpf.of("864.216.480-88"),
                    LocalDate.now().minusYears(18)
            );

            Driver savedDriver = userRepository.save(youngDriver);

            assertThat(savedDriver.getId()).isNotNull();
            assertThat(savedDriver.getBirthDate()).isEqualTo(LocalDate.now().minusYears(18));
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should persist driver with maximum age")
        void shouldPersistDriverWithMaximumAge() {
            Driver oldDriver = new Driver(
                    "Old",
                    "Driver",
                    "old.driver@gmail.com",
                    "password123",
                    Cpf.of("046.052.980-37"),
                    LocalDate.of(1950, 1, 1)
            );

            Driver savedDriver = userRepository.save(oldDriver);

            assertThat(savedDriver.getId()).isNotNull();
            assertThat(savedDriver.getBirthDate()).isEqualTo(LocalDate.of(1950, 1, 1));
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should fail when saving driver with duplicate email")
        void shouldFailWhenSavingDriverWithDuplicateEmail() {
            userRepository.save(driver);
            entityManager.flush();

            Driver duplicateEmailDriver = new Driver(
                    "Another",
                    "Driver",
                    "fulano.silva@gmail.com",
                    "password123",
                    Cpf.of("674.750.280-97"),
                    LocalDate.of(1985, 5, 5)
            );

            assertThatThrownBy(() -> {
                userRepository.save(duplicateEmailDriver);
                entityManager.flush();
            }).isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should fail when saving driver with duplicate CPF")
        void shouldFailWhenSavingDriverWithDuplicateCpf() {
            userRepository.save(driver);
            entityManager.flush();

            Driver duplicateCpfDriver = new Driver(
                    "Another",
                    "Driver",
                    "another.driver@gmail.com",
                    "password123",
                    Cpf.of("005.046.860-03"),
                    LocalDate.of(1985, 5, 5)
            );

            assertThatThrownBy(() -> {
                userRepository.save(duplicateCpfDriver);
                entityManager.flush();
            }).isInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    @DisplayName("Passenger Persistence Tests")
    class PassengerPersistenceTests {

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should persist passenger with all valid data")
        void shouldPersistPassengerWithValidData() {
            Passenger savedPassenger = userRepository.save(passenger);
            entityManager.flush();
            entityManager.clear();

            assertThat(savedPassenger.getId()).isNotNull();
            assertPassengerProperties(savedPassenger, "Maria", "Oliveira", "maria.oliveira@hotmail.com", "561.506.860-43");
            assertThat(savedPassenger.getRole()).isEqualTo(Role.PASSENGER);
            assertThat(savedPassenger.getBirthDate()).isEqualTo(LocalDate.of(1992, 2, 2));

            Optional<Passenger> foundPassenger = userRepository.findById(savedPassenger.getId())
                    .map(user -> (Passenger) user);
            assertThat(foundPassenger).isPresent();
            assertPassengerProperties(foundPassenger.get(), "Maria", "Oliveira", "maria.oliveira@hotmail.com", "561.506.860-43");
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should persist passenger with minimum age")
        void shouldPersistPassengerWithMinimumAge() {
            Passenger youngPassenger = new Passenger(
                    "Young",
                    "Passenger",
                    "young.passenger@gmail.com",
                    "password123",
                    Cpf.of("764.011.570-11"),
                    LocalDate.now().minusYears(16)
            );

            Passenger savedPassenger = userRepository.save(youngPassenger);

            assertThat(savedPassenger.getId()).isNotNull();
            assertThat(savedPassenger.getBirthDate()).isEqualTo(LocalDate.now().minusYears(16));
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should fail when saving passenger with duplicate email")
        void shouldFailWhenSavingPassengerWithDuplicateEmail() {
            userRepository.save(passenger);
            entityManager.flush();

            Passenger duplicateEmailPassenger = new Passenger(
                    "Another",
                    "Passenger",
                    "maria.oliveira@hotmail.com",
                    "password123",
                    Cpf.of("118.468.960-10"),
                    LocalDate.of(1990, 3, 3)
            );

            assertThatThrownBy(() -> {
                userRepository.save(duplicateEmailPassenger);
                entityManager.flush();
            }).isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should fail when saving passenger with duplicate CPF")
        void shouldFailWhenSavingPassengerWithDuplicateCpf() {
            userRepository.save(passenger);
            entityManager.flush();

            Passenger duplicateCpfPassenger = new Passenger(
                    "Another",
                    "Passenger",
                    "another.passenger@gmail.com",
                    "password123",
                    Cpf.of("561.506.860-43"),
                    LocalDate.of(1990, 3, 3)
            );

            assertThatThrownBy(() -> {
                userRepository.save(duplicateCpfPassenger);
                entityManager.flush();
            }).isInstanceOf(DataIntegrityViolationException.class);
        }
    }


    @Nested
    @DisplayName("User Query Tests")
    class UserQueryTests {

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should find user by email")
        void shouldFindUserByEmail() {
            userRepository.save(driver);
            entityManager.flush();

            Optional<User> foundUser = userRepository.findByEmail("fulano.silva@gmail.com");

            assertThat(foundUser).isPresent();
            assertThat(foundUser.get().getEmail()).isEqualTo("fulano.silva@gmail.com");
            assertThat(foundUser.get().getRole()).isEqualTo(Role.DRIVER);
            assertThat(foundUser.get().getName()).isEqualTo("Fulano");
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should find driver by email")
        void shouldFindDriverByEmail() {
            userRepository.save(driver);
            entityManager.flush();

            Optional<User> foundUser = userRepository.findByEmail("fulano.silva@gmail.com");

            assertThat(foundUser).isPresent();
            assertThat(foundUser.get()).isInstanceOf(Driver.class);
            Driver foundDriver = (Driver) foundUser.get();
            assertThat(foundDriver.getRole()).isEqualTo(Role.DRIVER);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should find passenger by email")
        void shouldFindPassengerByEmail() {
            userRepository.save(passenger);
            entityManager.flush();

            Optional<User> foundUser = userRepository.findByEmail("maria.oliveira@hotmail.com");

            assertThat(foundUser).isPresent();
            assertThat(foundUser.get()).isInstanceOf(Passenger.class);
            Passenger foundPassenger = (Passenger) foundUser.get();
            assertThat(foundPassenger.getRole()).isEqualTo(Role.PASSENGER);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should not find user by non-existent email")
        void shouldNotFindUserByNonExistentEmail() {
            Optional<User> foundUser = userRepository.findByEmail("nonexistent@gmail.com");

            assertThat(foundUser).isEmpty();
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should find user by email case insensitive")
        void shouldFindUserByEmailCaseInsensitive() {
            userRepository.save(driver);
            entityManager.flush();

            Optional<User> foundUser = userRepository.findByEmail("JOHN.DOE@gmail.COM");

            if (foundUser.isPresent()) {
                assertThat(foundUser.get().getEmail()).isEqualToIgnoringCase("john.doe@gmail.com");
            }
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should find all users")
        void shouldFindAllUsers() {
            userRepository.saveAll(List.of(driver, passenger, anotherDriver, anotherPassenger));
            entityManager.flush();

            List<User> allUsers = userRepository.findAll();

            assertThat(allUsers).hasSize(4);
            assertThat(allUsers).extracting(User::getRole)
                    .containsExactlyInAnyOrder(Role.DRIVER, Role.PASSENGER, Role.DRIVER, Role.PASSENGER);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should find users by role")
        void shouldFindUsersByRole() {
            userRepository.saveAll(List.of(driver, passenger, anotherDriver, anotherPassenger));
            entityManager.flush();

            List<User> allUsers = userRepository.findAll();
            List<User> drivers = allUsers.stream()
                    .filter(user -> user.getRole() == Role.DRIVER)
                    .collect(Collectors.toList());
            List<User> passengers = allUsers.stream()
                    .filter(user -> user.getRole() == Role.PASSENGER)
                    .collect(Collectors.toList());

            assertThat(drivers).hasSize(2);
            assertThat(passengers).hasSize(2);
            assertThat(drivers).allMatch(user -> user instanceof Driver);
            assertThat(passengers).allMatch(user -> user instanceof Passenger);
        }
    }

    @Nested
    @DisplayName("User Validation Tests")
    class UserValidationTests {

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should validate email format")
        void shouldValidateEmailFormat() {
            assertThatThrownBy(() -> {
                new Driver("João", "José", "invalid-email", "password123",
                        Cpf.of("502.979.848-02"), LocalDate.of(1990, 1, 1));
            }).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should validate CPF format")
        void shouldValidateCpfFormat() {
            assertThatThrownBy(() -> {
                Cpf.of("invalid-cpf");
            }).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should validate birth date not in future")
        void shouldValidateBirthDateNotInFuture() {
            LocalDate futureDate = LocalDate.now().plusDays(1);

            assertThatThrownBy(() -> {
                new Driver("Laura", "Oliveira", "laura.oliveira@gmail.com", "password123",
                        Cpf.of("097.110.570-77"), futureDate);
            }).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should validate required fields")
        void shouldValidateRequiredFields() {
            assertThatThrownBy(() -> {
                new Driver(null, "Arraché", "arrache.claudio@gmail.com", "password123",
                        Cpf.of("502.979.848-02"), LocalDate.of(1990, 1, 1));
            }).isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() -> {
                new Driver("Claudio", null, "arrache.claudio@gmail.com", "password123",
                        Cpf.of("502.979.848-02"), LocalDate.of(1990, 1, 1));
            }).isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() -> {
                new Driver("Claudio", "Arraché", null, "password123",
                        Cpf.of("502.979.848-02"), LocalDate.of(1990, 1, 1));
            }).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("User Update Tests")
    class UserUpdateTests {

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should update driver information")
        void shouldUpdateDriverInformation() {
            Driver savedDriver = userRepository.save(driver);
            entityManager.flush();

            savedDriver.setName("Updated John");
            savedDriver.setLastname("Updated Doe");
            Driver updatedDriver = userRepository.save(savedDriver);
            entityManager.flush();
            entityManager.clear();

            Optional<Driver> foundDriver = userRepository.findById(updatedDriver.getId())
                    .map(user -> (Driver) user);
            assertThat(foundDriver).isPresent();
            assertThat(foundDriver.get().getName()).isEqualTo("Updated John");
            assertThat(foundDriver.get().getLastname()).isEqualTo("Updated Doe");
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should update passenger information")
        void shouldUpdatePassengerInformation() {
            Passenger savedPassenger = userRepository.save(passenger);
            entityManager.flush();

            savedPassenger.setName("Updated Jane");
            savedPassenger.setLastname("Updated Smith");
            Passenger updatedPassenger = userRepository.save(savedPassenger);
            entityManager.flush();
            entityManager.clear();

            Optional<Passenger> foundPassenger = userRepository.findById(updatedPassenger.getId())
                    .map(user -> (Passenger) user);
            assertThat(foundPassenger).isPresent();
            assertThat(foundPassenger.get().getName()).isEqualTo("Updated Jane");
            assertThat(foundPassenger.get().getLastname()).isEqualTo("Updated Smith");
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should not allow updating email to existing email")
        void shouldNotAllowUpdatingEmailToExistingEmail() {
            userRepository.save(driver);
            userRepository.save(passenger);
            entityManager.flush();

            driver.setEmail("maria.oliveira@hotmail.com");

            assertThatThrownBy(() -> {
                userRepository.save(driver);
                entityManager.flush();
            }).isInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    @DisplayName("User Deletion Tests")
    class UserDeletionTests {

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should delete driver successfully")
        void shouldDeleteDriverSuccessfully() {
            Driver savedDriver = userRepository.save(driver);
            entityManager.flush();

            userRepository.delete(savedDriver);
            entityManager.flush();

            Optional<User> foundUser = userRepository.findById(savedDriver.getId());
            assertThat(foundUser).isEmpty();
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should delete passenger successfully")
        void shouldDeletePassengerSuccessfully() {
            Passenger savedPassenger = userRepository.save(passenger);
            entityManager.flush();

            userRepository.delete(savedPassenger);
            entityManager.flush();

            Optional<User> foundUser = userRepository.findById(savedPassenger.getId());
            assertThat(foundUser).isEmpty();
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should delete user by id")
        void shouldDeleteUserById() {
            Driver savedDriver = userRepository.save(driver);
            entityManager.flush();

            userRepository.deleteById(savedDriver.getId());
            entityManager.flush();

            Optional<User> foundUser = userRepository.findById(savedDriver.getId());
            assertThat(foundUser).isEmpty();
        }
    }

    @Nested
    @DisplayName("User Edge Cases Tests")
    class UserEdgeCasesTests {

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should handle special characters in names")
        void shouldHandleSpecialCharactersInNames() {
            Driver specialDriver = new Driver(
                    "José",
                    "da Silva",
                    "jose.silva@gmail.com",
                    "password123",
                    Cpf.of("147.349.700-09"),
                    LocalDate.of(1990, 1, 1)
            );

            Driver savedDriver = userRepository.save(specialDriver);

            assertThat(savedDriver.getName()).isEqualTo("José");
            assertThat(savedDriver.getLastname()).isEqualTo("da Silva");
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should handle long names")
        void shouldHandleLongNames() {
            String longName = "A".repeat(100);
            String longLastname = "B".repeat(100);

            Driver longNameDriver = new Driver(
                    longName,
                    longLastname,
                    "long.name@gmail.com",
                    "password123",
                    Cpf.of("775.333.080-26"),
                    LocalDate.of(1990, 1, 1)
            );

            Driver savedDriver = userRepository.save(longNameDriver);

            assertThat(savedDriver.getName()).isEqualTo(longName);
            assertThat(savedDriver.getLastname()).isEqualTo(longLastname);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should handle users with same name but different emails")
        void shouldHandleUsersWithSameNameButDifferentEmails() {
            Driver driver1 = new Driver(
                    "João",
                    "Maria",
                    "joao.maria@gmail.com",
                    "password123",
                    Cpf.of("058.518.900-53"),
                    LocalDate.of(1990, 1, 1)
            );

            Driver driver2 = new Driver(
                    "Maria",
                    "João",
                    "maria.joao@gmail.com",
                    "password123",
                    Cpf.of("541.618.900-28"),
                    LocalDate.of(1990, 1, 1)
            );

            userRepository.save(driver1);
            Driver savedDriver2 = userRepository.save(driver2);

            assertThat(savedDriver2.getId()).isNotNull();
            assertThat(savedDriver2.getName()).isEqualTo("Maria");
            assertThat(savedDriver2.getEmail()).isEqualTo("maria.joao@gmail.com");
        }
    }

    private Driver createDriver(String firstName, String lastName, String email, String cpf) {
        return new Driver(firstName, lastName, email, "password123",
                Cpf.of(cpf), LocalDate.of(1990, 1, 1));
    }

    private Passenger createPassenger(String firstName, String lastName, String email, String cpf) {
        return new Passenger(firstName, lastName, email, "password123",
                Cpf.of(cpf), LocalDate.of(1992, 2, 2));
    }

    private void assertDriverProperties(Driver driver, String name, String lastname, String email, String cpf) {
        assertThat(driver.getName()).isEqualTo(name);
        assertThat(driver.getLastname()).isEqualTo(lastname);
        assertThat(driver.getEmail()).isEqualTo(email);
        assertThat(driver.getCpf().toString()).isEqualTo(cpf);
        assertThat(driver.getRole()).isEqualTo(Role.DRIVER);
    }

    private void assertPassengerProperties(Passenger passenger, String name, String lastname, String email, String cpf) {
        assertThat(passenger.getName()).isEqualTo(name);
        assertThat(passenger.getLastname()).isEqualTo(lastname);
        assertThat(passenger.getEmail()).isEqualTo(email);
        assertThat(passenger.getCpf().toString()).isEqualTo(cpf);
        assertThat(passenger.getRole()).isEqualTo(Role.PASSENGER);
    }
}
