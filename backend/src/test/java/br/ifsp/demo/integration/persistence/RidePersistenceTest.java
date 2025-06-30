package br.ifsp.demo.integration.persistence;

import br.ifsp.demo.domain.*;
import br.ifsp.demo.repositories.CarRepository;
import br.ifsp.demo.repositories.DriverRepository;
import br.ifsp.demo.repositories.PassengerRepository;
import br.ifsp.demo.repositories.RideRepository;
import br.ifsp.demo.utils.RideStatus;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(br.ifsp.demo.config.TestConfig.class)
@ActiveProfiles("test")
@DisplayName("Ride Persistence Tests")
class RidePersistenceTest {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Driver driver;
    private Driver anotherDriver;
    private Car car;
    private Car anotherCar;
    private Passenger passenger;
    private Passenger anotherPassenger;
    private Address startAddress;
    private Address endAddress;
    private Address alternativeAddress;

    @BeforeEach
    void setUp() {
        entityManager.clear();

        driver = createDriver("Fulano", "Silva", "fulano.silva@gmail.com", "005.046.860-03");
        anotherDriver = createDriver("Ciclano", "Souza", "ciclano.souza@outlook.com", "355.553.060-75");

        car = createCar("Volkswagen", "Passat", "ABC-1234", driver);
        anotherCar = createCar("Chevrolet", "Malibu", "XYZ-5678", anotherDriver);

        passenger = createPassenger("Maria", "Oliveira", "maria.oliveira@hotmail.com", "561.506.860-43");
        anotherPassenger = createPassenger("Ana", "Souza", "ana.souza@yahoo.com", "936.138.620-42");

        startAddress = createAddress("Alameda das Hortências", "Centro", "123", "São Carlos");
        endAddress = createAddress("Rua das Orquídeas", "Jardins", "456", "São Carlos");
        alternativeAddress = createAddress("Alameda dos crisântemos", "Jardins", "789", "São Carlos");
    }

    @Nested
    @DisplayName("Ride Creation Tests")
    class RideCreationTests {

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should persist ride with all valid data")
        void shouldPersistRideWithValidData() {
            LocalDateTime departureTime = LocalDateTime.now().plusHours(1);
            Ride ride = new Ride(startAddress, endAddress, departureTime, driver, car);

            Ride savedRide = rideRepository.save(ride);
            entityManager.flush();
            entityManager.clear();

            assertThat(savedRide.getId()).isNotNull();
            assertRideProperties(savedRide, startAddress, endAddress, driver, car);
            assertThat(savedRide.getRideStatus()).isEqualTo(RideStatus.WAITING);
            assertThat(savedRide.getDepartureTime()).isEqualTo(departureTime);

            Optional<Ride> foundRide = rideRepository.findById(savedRide.getId());
            assertThat(foundRide).isPresent();
            assertRideProperties(foundRide.get(), startAddress, endAddress, driver, car);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should persist ride with future departure time")
        void shouldPersistRideWithFutureDepartureTime() {
            LocalDateTime futureDepartureTime = LocalDateTime.now().plusDays(1);
            Ride ride = new Ride(startAddress, endAddress, futureDepartureTime, driver, car);

            Ride savedRide = rideRepository.save(ride);

            assertThat(savedRide.getId()).isNotNull();
            assertThat(savedRide.getDepartureTime()).isEqualTo(futureDepartureTime);
            assertThat(savedRide.getRideStatus()).isEqualTo(RideStatus.WAITING);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should fail when trying to save ride without driver")
        void shouldFailWhenSavingRideWithoutDriver() {
            LocalDateTime departureTime = LocalDateTime.now().plusHours(1);
            Ride ride = new Ride(startAddress, endAddress, departureTime, null, car);

            assertThatThrownBy(() -> {
                rideRepository.save(ride);
                entityManager.flush();
            }).isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should fail when trying to save ride without car")
        void shouldFailWhenSavingRideWithoutCar() {
            LocalDateTime departureTime = LocalDateTime.now().plusHours(1);
            Ride ride = new Ride(startAddress, endAddress, departureTime, driver, null);

            assertThatThrownBy(() -> {
                rideRepository.save(ride);
                entityManager.flush();
            }).isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should fail when trying to save ride without addresses")
        void shouldFailWhenSavingRideWithoutAddresses() {
            LocalDateTime departureTime = LocalDateTime.now().plusHours(1);

            assertThatThrownBy(() -> {
                Ride ride = new Ride(null, endAddress, departureTime, driver, car);
                rideRepository.save(ride);
                entityManager.flush();
            }).isInstanceOf(DataIntegrityViolationException.class);

            assertThatThrownBy(() -> {
                Ride ride = new Ride(startAddress, null, departureTime, driver, car);
                rideRepository.save(ride);
                entityManager.flush();
            }).isInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    @DisplayName("Ride Query Tests")
    class RideQueryTests {

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should find rides by driver")
        void shouldFindRidesByDriver() {
            LocalDateTime departureTime1 = LocalDateTime.now().plusHours(1);
            LocalDateTime departureTime2 = LocalDateTime.now().plusHours(2);

            Ride ride1 = new Ride(startAddress, endAddress, departureTime1, driver, car);
            Ride ride2 = new Ride(endAddress, alternativeAddress, departureTime2, driver, car);
            Ride ride3 = new Ride(startAddress, endAddress, departureTime1, anotherDriver, anotherCar);

            rideRepository.saveAll(List.of(ride1, ride2, ride3));
            entityManager.flush();

            List<Ride> driverRides = rideRepository.findRideByDriver_Id(driver.getId());

            assertThat(driverRides).hasSize(2);
            assertThat(driverRides).allMatch(ride -> ride.getDriver().equals(driver));
            assertThat(driverRides).extracting(Ride::getDepartureTime)
                    .containsExactlyInAnyOrder(departureTime1, departureTime2);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should return empty list when driver has no rides")
        void shouldReturnEmptyListWhenDriverHasNoRides() {
            List<Ride> driverRides = rideRepository.findRideByDriver_Id(driver.getId());

            assertThat(driverRides).isEmpty();
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should find rides by status")
        void shouldFindRidesByStatus() {
            LocalDateTime departureTime = LocalDateTime.now().plusHours(1);
            Ride waitingRide = new Ride(startAddress, endAddress, departureTime, driver, car);
            Ride completedRide = new Ride(endAddress, alternativeAddress, departureTime, anotherDriver, anotherCar);
            completedRide.setRideStatus(RideStatus.FINISHED);

            rideRepository.saveAll(List.of(waitingRide, completedRide));
            entityManager.flush();

            List<Ride> allRides = rideRepository.findAll();
            List<Ride> waitingRides = allRides.stream()
                    .filter(ride -> ride.getRideStatus() == RideStatus.WAITING)
                    .collect(Collectors.toList());

            assertThat(waitingRides).hasSize(1);
            assertThat(waitingRides.get(0).getRideStatus()).isEqualTo(RideStatus.WAITING);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should find rides by departure time range")
        void shouldFindRidesByDepartureTimeRange() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime departureTime1 = now.plusHours(1);
            LocalDateTime departureTime2 = now.plusHours(2);
            LocalDateTime departureTime3 = now.plusDays(1);

            Ride ride1 = new Ride(startAddress, endAddress, departureTime1, driver, car);
            Ride ride2 = new Ride(endAddress, alternativeAddress, departureTime2, driver, car);
            Ride ride3 = new Ride(startAddress, alternativeAddress, departureTime3, anotherDriver, anotherCar);

            rideRepository.saveAll(List.of(ride1, ride2, ride3));
            entityManager.flush();

            LocalDateTime startRange = now;
            LocalDateTime endRange = now.plusHours(3);

            List<Ride> allRides = rideRepository.findAll();
            List<Ride> ridesInRange = allRides.stream()
                    .filter(ride -> ride.getDepartureTime().isAfter(startRange) &&
                            ride.getDepartureTime().isBefore(endRange))
                    .collect(Collectors.toList());

            assertThat(ridesInRange).hasSize(2);
            assertThat(ridesInRange).extracting(Ride::getDepartureTime)
                    .containsExactlyInAnyOrder(departureTime1, departureTime2);
        }
    }

    @Nested
    @DisplayName("Ride Passenger Management Tests")
    class RidePassengerManagementTests {

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should add passenger to ride successfully")
        void shouldAddPassengerToRideSuccessfully() {
            LocalDateTime departureTime = LocalDateTime.now().plusHours(1);
            Ride ride = new Ride(startAddress, endAddress, departureTime, driver, car);
            ride = rideRepository.save(ride);
            entityManager.flush();

            ride.addPassenger(passenger);
            ride = rideRepository.save(ride);
            entityManager.flush();
            entityManager.clear();

            List<Ride> passengerRides = rideRepository.findRideByPassengers_Id(passenger.getId());
            assertThat(passengerRides).hasSize(1);
            assertThat(passengerRides.get(0).getId()).isEqualTo(ride.getId());

            Optional<Ride> foundRide = rideRepository.findById(ride.getId());
            assertThat(foundRide).isPresent();

            Optional<Passenger> reloadedPassenger = passengerRepository.findById(passenger.getId());
            assertThat(reloadedPassenger).isPresent();
            assertThat(foundRide.get().getPassengers()).contains(reloadedPassenger.get());
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should add multiple passengers to ride")
        void shouldAddMultiplePassengersToRide() {
            LocalDateTime departureTime = LocalDateTime.now().plusHours(1);
            Ride ride = new Ride(startAddress, endAddress, departureTime, driver, car);
            ride = rideRepository.save(ride);

            ride.addPassenger(passenger);
            ride.addPassenger(anotherPassenger);
            ride = rideRepository.save(ride);
            entityManager.flush();

            Optional<Ride> foundRide = rideRepository.findById(ride.getId());
            assertThat(foundRide).isPresent();
            assertThat(foundRide.get().getPassengers()).hasSize(2);
            assertThat(foundRide.get().getPassengers()).containsExactlyInAnyOrder(passenger, anotherPassenger);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should remove passenger from ride")
        void shouldRemovePassengerFromRide() {
            LocalDateTime departureTime = LocalDateTime.now().plusHours(1);
            Ride ride = new Ride(startAddress, endAddress, departureTime, driver, car);
            ride.addPassenger(passenger);
            ride.addPassenger(anotherPassenger);
            ride = rideRepository.save(ride);
            entityManager.flush();

            ride.removePassenger(passenger.getId());
            ride = rideRepository.save(ride);
            entityManager.flush();
            entityManager.clear();

            Optional<Ride> foundRide = rideRepository.findById(ride.getId());
            Optional<Passenger> reloadedPassenger = passengerRepository.findById(passenger.getId());
            Optional<Passenger> reloadedAnotherPassenger = passengerRepository.findById(anotherPassenger.getId());

            assertThat(foundRide).isPresent();
            assertThat(reloadedPassenger).isPresent();
            assertThat(reloadedAnotherPassenger).isPresent();

            assertThat(foundRide.get().getPassengers()).hasSize(1);
            assertThat(foundRide.get().getPassengers()).contains(reloadedAnotherPassenger.get());
            assertThat(foundRide.get().getPassengers()).doesNotContain(reloadedPassenger.get());
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should not exceed car capacity")
        void shouldNotExceedCarCapacity() {
            LocalDateTime departureTime = LocalDateTime.now().plusHours(1);
            Ride ride = new Ride(startAddress, endAddress, departureTime, driver, car);
            ride = rideRepository.save(ride);

            String[] validCpfs = {
                    "324.557.110-05",
                    "507.795.490-90",
                    "443.583.480-41",
                    "077.564.530-34"
            };

            for (int i = 0; i < 4; i++) {
                Passenger tempPassenger = createPassenger("Passenger" + i, "Test",
                        "passenger" + i + "@test.com", validCpfs[i]);
                ride.addPassenger(tempPassenger);
            }

            assertThat(ride.getPassengers()).hasSize(4);

            Passenger fifthPassenger = createPassenger("Fifth", "Passenger",
                    "fifth@test.com", "754.630.960-31");
        }
    }

    @Nested
    @DisplayName("Ride Status Management Tests")
    class RideStatusManagementTests {

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should update ride status successfully")
        void shouldUpdateRideStatusSuccessfully() {
            LocalDateTime departureTime = LocalDateTime.now().plusHours(1);
            Ride ride = new Ride(startAddress, endAddress, departureTime, driver, car);
            ride = rideRepository.save(ride);
            entityManager.flush();

            assertThat(ride.getRideStatus()).isEqualTo(RideStatus.WAITING);

            ride.setRideStatus(RideStatus.STARTED);
            ride = rideRepository.save(ride);
            entityManager.flush();
            entityManager.clear();

            Optional<Ride> foundRide = rideRepository.findById(ride.getId());
            assertThat(foundRide).isPresent();
            assertThat(foundRide.get().getRideStatus()).isEqualTo(RideStatus.STARTED);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should complete ride successfully")
        void shouldCompleteRideSuccessfully() {
            LocalDateTime departureTime = LocalDateTime.now().plusHours(1);
            Ride ride = new Ride(startAddress, endAddress, departureTime, driver, car);
            ride.addPassenger(passenger);
            ride = rideRepository.save(ride);

            ride.setRideStatus(RideStatus.FINISHED);
            ride = rideRepository.save(ride);
            entityManager.flush();

            Optional<Ride> foundRide = rideRepository.findById(ride.getId());
            assertThat(foundRide).isPresent();
            assertThat(foundRide.get().getRideStatus()).isEqualTo(RideStatus.FINISHED);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should cancel ride successfully")
        void shouldCancelRideSuccessfully() {
            LocalDateTime departureTime = LocalDateTime.now().plusHours(1);
            Ride ride = new Ride(startAddress, endAddress, departureTime, driver, car);
            ride = rideRepository.save(ride);

            ride.setRideStatus(RideStatus.CANCELLED);
            ride = rideRepository.save(ride);
            entityManager.flush();

            Optional<Ride> foundRide = rideRepository.findById(ride.getId());
            assertThat(foundRide).isPresent();
            assertThat(foundRide.get().getRideStatus()).isEqualTo(RideStatus.CANCELLED);
        }
    }

    @Nested
    @DisplayName("Ride Business Logic Tests")
    class RideBusinessLogicTests {

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should check if ride exists for driver and car")
        void shouldCheckIfRideExistsForDriverAndCar() {
            LocalDateTime departureTime = LocalDateTime.now().plusHours(1);
            Ride ride = new Ride(startAddress, endAddress, departureTime, driver, car);
            rideRepository.save(ride);
            entityManager.flush();

            boolean exists = rideRepository.existsByDriverIdAndCarId(driver.getId(), car.getId());
            boolean notExists = rideRepository.existsByDriverIdAndCarId(anotherDriver.getId(), anotherCar.getId());

            assertThat(exists).isTrue();
            assertThat(notExists).isFalse();
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should handle ride with same start and end address")
        void shouldHandleRideWithSameStartAndEndAddress() {
            LocalDateTime departureTime = LocalDateTime.now().plusHours(1);
            Ride ride = new Ride(startAddress, startAddress, departureTime, driver, car);

            assertThatCode(() -> {
                rideRepository.save(ride);
                entityManager.flush();
            }).doesNotThrowAnyException();
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should delete ride successfully")
        void shouldDeleteRideSuccessfully() {
            LocalDateTime departureTime = LocalDateTime.now().plusHours(1);
            Ride ride = new Ride(startAddress, endAddress, departureTime, driver, car);
            Ride savedRide = rideRepository.save(ride);
            entityManager.flush();

            rideRepository.delete(savedRide);
            entityManager.flush();

            Optional<Ride> foundRide = rideRepository.findById(savedRide.getId());
            assertThat(foundRide).isEmpty();
        }
    }

    private Driver createDriver(String firstName, String lastName, String email, String cpf) {
        Driver driver = new Driver(firstName, lastName, email, "password123",
                Cpf.of(cpf), LocalDate.of(1990, 1, 1));
        return driverRepository.save(driver);
    }

    private Car createCar(String brand, String model, String licensePlate, Driver driver) {
        Car car = new Car(brand, model, "Black", 5, LicensePlate.parse(licensePlate));
        car.setDriver(driver);
        return carRepository.save(car);
    }

    private Passenger createPassenger(String firstName, String lastName, String email, String cpf) {
        Passenger passenger = new Passenger(firstName, lastName, email, "password123",
                Cpf.of(cpf), LocalDate.of(1992, 2, 2));
        return passengerRepository.save(passenger);
    }

    private Address createAddress(String street, String neighborhood, String number, String city) {
        return Address.builder()
                .street(street)
                .neighborhood(neighborhood)
                .number(number)
                .city(city)
                .build();
    }

    private void assertRideProperties(Ride ride, Address startAddr, Address endAddr, Driver driver, Car car) {
        assertThat(ride.getStartAddress()).isEqualTo(startAddr);
        assertThat(ride.getEndAddress()).isEqualTo(endAddr);
        assertThat(ride.getDriver()).isEqualTo(driver);
        assertThat(ride.getCar()).isEqualTo(car);
    }
}
