package br.ifsp.demo.integration.persistence;

import br.ifsp.demo.domain.Car;
import br.ifsp.demo.domain.Cpf;
import br.ifsp.demo.domain.Driver;
import br.ifsp.demo.domain.LicensePlate;
import br.ifsp.demo.repositories.CarRepository;
import br.ifsp.demo.repositories.DriverRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import java.util.stream.Collectors;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(br.ifsp.demo.config.TestConfig.class)
@ActiveProfiles("test")
@DisplayName("Car Persistence Tests")
class CarPersistenceTest {
    @Autowired
    private CarRepository carRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Driver driver;
    private Driver anotherDriver;

    @BeforeEach
    void setUp() {
        entityManager.clear();

        driver = createDriver("Fulano", "Silva", "fulano.silva@gmail.com", "005.046.860-03");
        anotherDriver = createDriver("Ciclano", "Souza", "ciclano.souza@outlook.com", "355.553.060-75");
    }

    @Nested
    @DisplayName("Car Creation Tests")
    class CarCreationTests {

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should persist car with all valid data")
        void shouldPersistCarWithValidData() {
            Car car = createValidCar("Volkswagen", "Passat", "ABC-1234");
            car.setDriver(driver);

            Car savedCar = carRepository.save(car);
            entityManager.flush();
            entityManager.clear();

            assertThat(savedCar.getId()).isNotNull();
            assertCarProperties(savedCar, "Volkswagen", "Passat", "Black", 5, "ABC-1234");
            assertThat(savedCar.getDriver()).isEqualTo(driver);
            assertThat(savedCar.getIsActive()).isTrue();

            Optional<Car> foundCar = carRepository.findById(savedCar.getId());
            assertThat(foundCar).isPresent();
            assertCarProperties(foundCar.get(), "Volkswagen", "Passat", "Black", 5, "ABC-1234");
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should persist car with minimum required data")
        void shouldPersistCarWithMinimumData() {
            Car car = new Car("Chevrolet", "Malibu", "White", 4, LicensePlate.parse("XYZ-9876"));
            car.setDriver(driver);
            Car savedCar = carRepository.save(car);

            assertThat(savedCar.getId()).isNotNull();
            assertThat(savedCar.getIsActive()).isTrue();
            assertThat(savedCar.getDriver()).isEqualTo(driver);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should fail when trying to save car without driver")
        void shouldFailWhenSavingCarWithoutDriver() {
            Car car = createValidCar("Volkswagen", "Passat", "ABC-1234");

            assertThatThrownBy(() -> {
                carRepository.save(car);
                entityManager.flush();
            }).isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should fail when trying to save car with duplicate license plate")
        void shouldFailWhenSavingCarWithDuplicateLicensePlate() {
            Car firstCar = createValidCar("Volkswagen", "Passat", "ABC-1234");
            firstCar.setDriver(driver);
            carRepository.save(firstCar);
            entityManager.flush();

            Car secondCar = createValidCar("Chevrolet", "Malibu", "ABC-1234");
            secondCar.setDriver(anotherDriver);

            assertThatThrownBy(() -> {
                carRepository.save(secondCar);
                entityManager.flush();
            }).isInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    @DisplayName("Car Query Tests")
    class CarQueryTests {

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should find car by license plate")
        void shouldFindCarByLicensePlate() {
            Car car = createValidCar("Volkswagen", "Passat", "ABC-1234");
            car.setDriver(driver);
            carRepository.save(car);
            entityManager.flush();
            entityManager.clear();

            Optional<Car> foundCar = carRepository.findByLicensePlate(LicensePlate.parse("ABC-1234"));

            assertThat(foundCar).isPresent();
            assertCarProperties(foundCar.get(), "Volkswagen", "Passat", "Black", 5, "ABC-1234");
            assertThat(foundCar.get().getDriver()).isEqualTo(driver);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should return empty when searching for non-existent license plate")
        void shouldReturnEmptyForNonExistentLicensePlate() {
            Optional<Car> foundCar = carRepository.findByLicensePlate(LicensePlate.parse("XXX-0000"));

            assertThat(foundCar).isEmpty();
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should find cars by driver")
        void shouldFindCarsByDriver() {
            Car car1 = createValidCar("Volkswagen", "Passat", "ABC-1234");
            car1.setDriver(driver);
            Car car2 = createValidCar("Chevrolet", "Malibu", "XYZ-5678");
            car2.setDriver(driver);
            Car car3 = createValidCar("Ford", "Focus", "DEF-9012");
            car3.setDriver(anotherDriver);

            carRepository.saveAll(List.of(car1, car2, car3));
            entityManager.flush();

            List<Car> allCars = carRepository.findAll();
            List<Car> driverCars = allCars.stream()
                    .filter(car -> car.getDriver().equals(driver))
                    .collect(Collectors.toList());

            assertThat(driverCars).hasSize(2);
            assertThat(driverCars).extracting(Car::getLicensePlate)
                    .containsExactlyInAnyOrder(
                            LicensePlate.parse("ABC-1234"),
                            LicensePlate.parse("XYZ-5678")
                    );
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should find only active cars")
        void shouldFindOnlyActiveCars() {
            Car activeCar = createValidCar("Volkswagen", "Passat", "ABC-1234");
            activeCar.setDriver(driver);

            Car inactiveCar = createValidCar("Chevrolet", "Malibu", "XYZ-5678");
            inactiveCar.setDriver(driver);
            inactiveCar.deactivate();

            carRepository.saveAll(List.of(activeCar, inactiveCar));
            entityManager.flush();

            List<Car> allCars = carRepository.findAll();
            List<Car> activeCars = allCars.stream()
                    .filter(Car::getIsActive)
                    .collect(Collectors.toList());

            assertThat(activeCars).hasSize(1);
            assertThat(activeCars.get(0).getLicensePlate()).isEqualTo(LicensePlate.parse("ABC-1234"));
        }
    }

    @Nested
    @DisplayName("Car State Management Tests")
    class CarStateManagementTests {

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should deactivate car successfully")
        void shouldDeactivateCarSuccessfully() {
            Car car = createValidCar("Volkswagen", "Passat", "ABC-1234");
            car.setDriver(driver);
            Car savedCar = carRepository.save(car);
            entityManager.flush();

            assertThat(savedCar.getIsActive()).isTrue();

            savedCar.deactivate();
            carRepository.save(savedCar);
            entityManager.flush();
            entityManager.clear();

            Optional<Car> foundCar = carRepository.findById(savedCar.getId());
            assertThat(foundCar).isPresent();
            assertThat(foundCar.get().getIsActive()).isFalse();
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should activate car successfully")
        void shouldActivateCarSuccessfully() {
            Car car = createValidCar("Volkswagen", "Passat", "ABC-1234");
            car.setDriver(driver);
            car.deactivate();
            Car savedCar = carRepository.save(car);
            entityManager.flush();

            assertThat(savedCar.getIsActive()).isFalse();

            savedCar.setIsActive(true);
            carRepository.save(savedCar);
            entityManager.flush();
            entityManager.clear();

            Optional<Car> foundCar = carRepository.findById(savedCar.getId());
            assertThat(foundCar).isPresent();
            assertThat(foundCar.get().getIsActive()).isTrue();
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should update car properties")
        void shouldUpdateCarProperties() {
            Car car = createValidCar("Volkswagen", "Passat", "ABC-1234");
            car.setDriver(driver);
            Car savedCar = carRepository.save(car);
            entityManager.flush();

            savedCar.setColor("Red");
            savedCar.setSeats(4);
            carRepository.save(savedCar);
            entityManager.flush();
            entityManager.clear();

            Optional<Car> foundCar = carRepository.findById(savedCar.getId());
            assertThat(foundCar).isPresent();
            assertThat(foundCar.get().getColor()).isEqualTo("Red");
            assertThat(foundCar.get().getSeats()).isEqualTo(4);
        }
    }

    @Nested
    @DisplayName("Car Validation Tests")
    class CarValidationTests {

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should validate license plate format")
        void shouldValidateLicensePlateFormat() {
            assertThatThrownBy(() -> LicensePlate.parse("INVALID"))
                    .isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() -> LicensePlate.parse("ABC-12345"))
                    .isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() -> LicensePlate.parse("ABCD-123"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should accept valid license plate formats")
        void shouldAcceptValidLicensePlateFormats() {
            assertThatCode(() -> LicensePlate.parse("ABC-1234")).doesNotThrowAnyException();
            assertThatCode(() -> LicensePlate.parse("XYZ9B76")).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Car Repository Edge Cases")
    class CarRepositoryEdgeCasesTests {

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should handle concurrent updates correctly")
        void shouldHandleConcurrentUpdatesCorrectly() {
            Car car = createValidCar("Volkswagen", "Passat", "ABC-1234");
            car.setDriver(driver);
            Car savedCar = carRepository.save(car);
            entityManager.flush();
            entityManager.clear();

            Car car1 = carRepository.findById(savedCar.getId()).orElseThrow();
            Car car2 = carRepository.findById(savedCar.getId()).orElseThrow();

            car1.setColor("Red");
            car2.setSeats(3);

            carRepository.save(car1);
            entityManager.flush();

            carRepository.save(car2);
            entityManager.flush();
            entityManager.clear();

            Car finalCar = carRepository.findById(savedCar.getId()).orElseThrow();
            assertThat(finalCar.getSeats()).isEqualTo(3);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should delete car successfully")
        void shouldDeleteCarSuccessfully() {
            Car car = createValidCar("Volkswagen", "Passat", "ABC-1234");
            car.setDriver(driver);
            Car savedCar = carRepository.save(car);
            entityManager.flush();

            carRepository.delete(savedCar);
            entityManager.flush();

            Optional<Car> foundCar = carRepository.findById(savedCar.getId());
            assertThat(foundCar).isEmpty();
        }
    }

    private Driver createDriver(String firstName, String lastName, String email, String cpf) {
        Driver driver = new Driver(
                firstName,
                lastName,
                email,
                "password123",
                Cpf.of(cpf),
                LocalDate.of(1990, 1, 1)
        );
        return driverRepository.save(driver);
    }

    private Car createValidCar(String brand, String model, String licensePlate) {
        return new Car(brand, model, "Black", 5, LicensePlate.parse(licensePlate));
    }

    private void assertCarProperties(Car car, String brand, String model, String color, int seats, String licensePlate) {
        assertThat(car.getBrand()).isEqualTo(brand);
        assertThat(car.getModel()).isEqualTo(model);
        assertThat(car.getColor()).isEqualTo(color);
        assertThat(car.getSeats()).isEqualTo(seats);
        assertThat(car.getLicensePlate().toString()).isEqualTo(licensePlate);
    }
}
