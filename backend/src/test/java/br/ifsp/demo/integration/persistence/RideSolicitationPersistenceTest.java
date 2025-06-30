package br.ifsp.demo.integration.persistence;

import br.ifsp.demo.domain.*;
import br.ifsp.demo.repositories.*;
import br.ifsp.demo.utils.RideSolicitationStatus;
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
@DisplayName("RideSolicitation Persistence Tests")
class RideSolicitationPersistenceTest {

    @Autowired
    private RideSolicitationRepository solicitationRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private CarRepository carRepository;

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
    private Ride ride;
    private Ride anotherRide;
    private Address startAddress;
    private Address endAddress;

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

        ride = createRide(startAddress, endAddress, driver, car);
        anotherRide = createRide(endAddress, startAddress, anotherDriver, anotherCar);
    }

    @Nested
    @DisplayName("RideSolicitation Creation Tests")
    class RideSolicitationCreationTests {

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should persist ride solicitation with valid data")
        void shouldPersistRideSolicitationWithValidData() {
            RideSolicitation solicitation = new RideSolicitation(ride, passenger);

            RideSolicitation savedSolicitation = solicitationRepository.save(solicitation);
            entityManager.flush();
            entityManager.clear();

            assertThat(savedSolicitation.getId()).isNotNull();
            assertThat(savedSolicitation.getStatus()).isEqualTo(RideSolicitationStatus.WAITING);

            Optional<RideSolicitation> foundSolicitation = solicitationRepository.findById(savedSolicitation.getId());
            assertThat(foundSolicitation).isPresent();

            Optional<Ride> reloadedRide = rideRepository.findById(ride.getId());
            Optional<Passenger> reloadedPassenger = passengerRepository.findById(passenger.getId());

            assertThat(reloadedRide).isPresent();
            assertThat(reloadedPassenger).isPresent();

            assertThat(foundSolicitation.get().getRide()).isEqualTo(reloadedRide.get());
            assertThat(foundSolicitation.get().getPassenger()).isEqualTo(reloadedPassenger.get());
            assertThat(foundSolicitation.get().getStatus()).isEqualTo(RideSolicitationStatus.WAITING);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should create multiple solicitations for same ride")
        void shouldCreateMultipleSolicitationsForSameRide() {
            RideSolicitation solicitation1 = new RideSolicitation(ride, passenger);
            RideSolicitation solicitation2 = new RideSolicitation(ride, anotherPassenger);

            solicitationRepository.save(solicitation1);
            solicitationRepository.save(solicitation2);
            entityManager.flush();

            List<RideSolicitation> rideSolicitations = solicitationRepository.findRideSolicitationByRide_Id(ride.getId());
            assertThat(rideSolicitations).hasSize(2);
            assertThat(rideSolicitations).extracting(RideSolicitation::getPassenger)
                    .containsExactlyInAnyOrder(passenger, anotherPassenger);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should create multiple solicitations for same passenger")
        void shouldCreateMultipleSolicitationsForSamePassenger() {
            RideSolicitation solicitation1 = new RideSolicitation(ride, passenger);
            RideSolicitation solicitation2 = new RideSolicitation(anotherRide, passenger);

            solicitationRepository.save(solicitation1);
            solicitationRepository.save(solicitation2);
            entityManager.flush();

            List<RideSolicitation> passengerSolicitations = solicitationRepository.findRideSolicitationsByPassenger_Id(passenger.getId());
            assertThat(passengerSolicitations).hasSize(2);
            assertThat(passengerSolicitations).extracting(RideSolicitation::getRide)
                    .containsExactlyInAnyOrder(ride, anotherRide);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should fail when trying to save solicitation without ride")
        void shouldFailWhenSavingSolicitationWithoutRide() {
            assertThatThrownBy(() -> new RideSolicitation(null, passenger))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should fail when trying to create solicitation without passenger")
        void shouldFailWhenCreatingSolicitationWithoutPassenger() {
            assertThatThrownBy(() -> {
                new RideSolicitation(ride, null);
            }).isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Ride and passenger must not be null");
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should prevent duplicate solicitation for same ride and passenger")
        void shouldPreventDuplicateSolicitationForSameRideAndPassenger() {
            RideSolicitation solicitation1 = new RideSolicitation(ride, passenger);
            RideSolicitation solicitation2 = new RideSolicitation(ride, passenger);

            solicitationRepository.save(solicitation1);
            entityManager.flush();

            assertThatThrownBy(() -> {
                solicitationRepository.save(solicitation2);
                entityManager.flush();
            }).isInstanceOf(DataIntegrityViolationException.class);
        }
    }

    private Driver createDriver(String firstName, String lastName, String email, String cpf) {
        Driver driver = new Driver(firstName, lastName, email, "password123",
                Cpf.of(cpf), LocalDate.of(1990, 1, 1));
        return driverRepository.save(driver);
    }

    @Nested
    @DisplayName("RideSolicitation Query Tests")
    class RideSolicitationQueryTests {

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should find solicitations by ride")
        void shouldFindSolicitationsByRide() {
            RideSolicitation solicitation1 = new RideSolicitation(ride, passenger);
            RideSolicitation solicitation2 = new RideSolicitation(ride, anotherPassenger);
            RideSolicitation solicitation3 = new RideSolicitation(anotherRide, passenger);

            solicitationRepository.saveAll(List.of(solicitation1, solicitation2, solicitation3));
            entityManager.flush();

            List<RideSolicitation> rideSolicitations = solicitationRepository.findRideSolicitationByRide_Id(ride.getId());

            assertThat(rideSolicitations).hasSize(2);
            assertThat(rideSolicitations).allMatch(s -> s.getRide().equals(ride));
            assertThat(rideSolicitations).extracting(RideSolicitation::getPassenger)
                    .containsExactlyInAnyOrder(passenger, anotherPassenger);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should find solicitations by passenger")
        void shouldFindSolicitationsByPassenger() {
            RideSolicitation solicitation1 = new RideSolicitation(ride, passenger);
            RideSolicitation solicitation2 = new RideSolicitation(anotherRide, passenger);
            RideSolicitation solicitation3 = new RideSolicitation(ride, anotherPassenger);

            solicitationRepository.saveAll(List.of(solicitation1, solicitation2, solicitation3));
            entityManager.flush();

            List<RideSolicitation> passengerSolicitations = solicitationRepository.findRideSolicitationsByPassenger_Id(passenger.getId());

            assertThat(passengerSolicitations).hasSize(2);
            assertThat(passengerSolicitations).allMatch(s -> s.getPassenger().equals(passenger));
            assertThat(passengerSolicitations).extracting(RideSolicitation::getRide)
                    .containsExactlyInAnyOrder(ride, anotherRide);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should return empty list when ride has no solicitations")
        void shouldReturnEmptyListWhenRideHasNoSolicitations() {
            List<RideSolicitation> rideSolicitations = solicitationRepository.findRideSolicitationByRide_Id(ride.getId());

            assertThat(rideSolicitations).isEmpty();
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should return empty list when passenger has no solicitations")
        void shouldReturnEmptyListWhenPassengerHasNoSolicitations() {
            List<RideSolicitation> passengerSolicitations = solicitationRepository.findRideSolicitationsByPassenger_Id(passenger.getId());

            assertThat(passengerSolicitations).isEmpty();
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should find solicitations by status")
        void shouldFindSolicitationsByStatus() {
            RideSolicitation waitingSolicitation = new RideSolicitation(ride, passenger);
            RideSolicitation acceptedSolicitation = new RideSolicitation(anotherRide, anotherPassenger);
            acceptedSolicitation.setStatus(RideSolicitationStatus.ACCEPTED);

            solicitationRepository.saveAll(List.of(waitingSolicitation, acceptedSolicitation));
            entityManager.flush();

            List<RideSolicitation> allSolicitations = solicitationRepository.findAll();
            List<RideSolicitation> waitingSolicitations = allSolicitations.stream()
                    .filter(s -> s.getStatus() == RideSolicitationStatus.WAITING)
                    .collect(Collectors.toList());

            assertThat(waitingSolicitations).hasSize(1);
            assertThat(waitingSolicitations.get(0).getStatus()).isEqualTo(RideSolicitationStatus.WAITING);
        }
    }

    @Nested
    @DisplayName("RideSolicitation Business Logic Tests")
    class RideSolicitationBusinessLogicTests {

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should delete solicitation successfully")
        void shouldDeleteSolicitationSuccessfully() {
            RideSolicitation solicitation = new RideSolicitation(ride, passenger);
            RideSolicitation savedSolicitation = solicitationRepository.save(solicitation);
            entityManager.flush();

            solicitationRepository.delete(savedSolicitation);
            entityManager.flush();

            Optional<RideSolicitation> foundSolicitation = solicitationRepository.findById(savedSolicitation.getId());
            assertThat(foundSolicitation).isEmpty();
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should maintain referential integrity when deleting ride")
        void shouldMaintainReferentialIntegrityWhenDeletingRide() {
            RideSolicitation solicitation = new RideSolicitation(ride, passenger);
            solicitationRepository.save(solicitation);
            entityManager.flush();

            assertThatThrownBy(() -> {
                rideRepository.delete(ride);
                entityManager.flush();
            }).isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should maintain referential integrity when deleting passenger")
        void shouldMaintainReferentialIntegrityWhenDeletingPassenger() {
            RideSolicitation solicitation = new RideSolicitation(ride, passenger);
            solicitationRepository.save(solicitation);
            entityManager.flush();

            assertThatThrownBy(() -> {
                passengerRepository.delete(passenger);
                entityManager.flush();
            }).isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should count solicitations by ride")
        void shouldCountSolicitationsByRide() {
            RideSolicitation solicitation1 = new RideSolicitation(ride, passenger);
            RideSolicitation solicitation2 = new RideSolicitation(ride, anotherPassenger);

            solicitationRepository.saveAll(List.of(solicitation1, solicitation2));
            entityManager.flush();

            List<RideSolicitation> rideSolicitations = solicitationRepository.findRideSolicitationByRide_Id(ride.getId());
            assertThat(rideSolicitations).hasSize(2);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should count solicitations by passenger")
        void shouldCountSolicitationsByPassenger() {
            RideSolicitation solicitation1 = new RideSolicitation(ride, passenger);
            RideSolicitation solicitation2 = new RideSolicitation(anotherRide, passenger);

            solicitationRepository.saveAll(List.of(solicitation1, solicitation2));
            entityManager.flush();

            List<RideSolicitation> passengerSolicitations = solicitationRepository.findRideSolicitationsByPassenger_Id(passenger.getId());
            assertThat(passengerSolicitations).hasSize(2);
        }
    }
    
    @Nested
    @DisplayName("RideSolicitation Status Management Tests")
    class RideSolicitationStatusManagementTests {

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should update solicitation status successfully")
        void shouldUpdateSolicitationStatusSuccessfully() {
            RideSolicitation solicitation = new RideSolicitation(ride, passenger);
            solicitation = solicitationRepository.save(solicitation);
            entityManager.flush();

            assertThat(solicitation.getStatus()).isEqualTo(RideSolicitationStatus.WAITING);

            solicitation.setStatus(RideSolicitationStatus.ACCEPTED);
            solicitation = solicitationRepository.save(solicitation);
            entityManager.flush();
            entityManager.clear();

            Optional<RideSolicitation> foundSolicitation = solicitationRepository.findById(solicitation.getId());
            assertThat(foundSolicitation).isPresent();
            assertThat(foundSolicitation.get().getStatus()).isEqualTo(RideSolicitationStatus.ACCEPTED);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should reject solicitation successfully")
        void shouldRejectSolicitationSuccessfully() {
            RideSolicitation solicitation = new RideSolicitation(ride, passenger);
            solicitation = solicitationRepository.save(solicitation);

            solicitation.setStatus(RideSolicitationStatus.REJECTED);
            solicitation = solicitationRepository.save(solicitation);
            entityManager.flush();

            Optional<RideSolicitation> foundSolicitation = solicitationRepository.findById(solicitation.getId());
            assertThat(foundSolicitation).isPresent();
            assertThat(foundSolicitation.get().getStatus()).isEqualTo(RideSolicitationStatus.REJECTED);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should cancel solicitation successfully")
        void shouldCancelSolicitationSuccessfully() {
            RideSolicitation solicitation = new RideSolicitation(ride, passenger);
            solicitation = solicitationRepository.save(solicitation);

            solicitation.setStatus(RideSolicitationStatus.CANCELLED);
            solicitation = solicitationRepository.save(solicitation);
            entityManager.flush();

            Optional<RideSolicitation> foundSolicitation = solicitationRepository.findById(solicitation.getId());
            assertThat(foundSolicitation).isPresent();
            assertThat(foundSolicitation.get().getStatus()).isEqualTo(RideSolicitationStatus.CANCELLED);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should handle status transitions correctly")
        void shouldHandleStatusTransitionsCorrectly() {
            RideSolicitation solicitation = new RideSolicitation(ride, passenger);
            solicitation = solicitationRepository.save(solicitation);

            solicitation.setStatus(RideSolicitationStatus.ACCEPTED);
            solicitation = solicitationRepository.save(solicitation);

            solicitation.setStatus(RideSolicitationStatus.CANCELLED);
            solicitation = solicitationRepository.save(solicitation);
            entityManager.flush();

            Optional<RideSolicitation> foundSolicitation = solicitationRepository.findById(solicitation.getId());
            assertThat(foundSolicitation).isPresent();
            assertThat(foundSolicitation.get().getStatus()).isEqualTo(RideSolicitationStatus.CANCELLED);
        }
    }
    
    @Nested
    @DisplayName("RideSolicitation Edge Cases Tests")
    class RideSolicitationEdgeCasesTests {

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should handle concurrent solicitation creation")
        void shouldHandleConcurrentSolicitationCreation() {
            RideSolicitation solicitation1 = new RideSolicitation(ride, passenger);
            RideSolicitation solicitation2 = new RideSolicitation(ride, anotherPassenger);

            solicitationRepository.save(solicitation1);
            solicitationRepository.save(solicitation2);
            entityManager.flush();

            List<RideSolicitation> rideSolicitations = solicitationRepository.findRideSolicitationByRide_Id(ride.getId());
            assertThat(rideSolicitations).hasSize(2);
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should handle solicitation for ride with no available seats")
        void shouldHandleSolicitationForRideWithNoAvailableSeats() {
            RideSolicitation solicitation = new RideSolicitation(ride, passenger);

            assertThatCode(() -> {
                solicitationRepository.save(solicitation);
                entityManager.flush();
            }).doesNotThrowAnyException();
        }

        @Test
        @Tag("IntegrationTest")
        @Tag("PersistenceTest")
        @DisplayName("Should handle solicitation status validation")
        void shouldHandleSolicitationStatusValidation() {
            RideSolicitation solicitation = new RideSolicitation(ride, passenger);
            solicitation = solicitationRepository.save(solicitation);

            assertThat(solicitation.getStatus()).isEqualTo(RideSolicitationStatus.WAITING);

            solicitation.setStatus(RideSolicitationStatus.ACCEPTED);
            solicitationRepository.save(solicitation);

            Optional<RideSolicitation> foundSolicitation = solicitationRepository.findById(solicitation.getId());
            assertThat(foundSolicitation).isPresent();
            assertThat(foundSolicitation.get().getStatus()).isEqualTo(RideSolicitationStatus.ACCEPTED);
        }
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
    
    private Ride createRide(Address startAddress, Address endAddress, Driver driver, Car car) {
        LocalDateTime departureTime = LocalDateTime.now().plusHours(1);
        Ride ride = new Ride(startAddress, endAddress, departureTime, driver, car);
        return rideRepository.save(ride);
    }
    
    private void assertSolicitationProperties(RideSolicitation solicitation, Ride expectedRide,
                                              Passenger expectedPassenger, RideSolicitationStatus expectedStatus) {
        assertThat(solicitation.getRide()).isEqualTo(expectedRide);
        assertThat(solicitation.getPassenger()).isEqualTo(expectedPassenger);
        assertThat(solicitation.getStatus()).isEqualTo(expectedStatus);
    }
}
