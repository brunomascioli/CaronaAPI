package br.ifsp.demo.integration.api.utils;


import br.ifsp.demo.repositories.*;
import br.ifsp.demo.security.user.JpaUserRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class BaseApiIntegrationTest {

    @LocalServerPort protected int port;
    @Autowired private JpaUserRepository userRepository;
    @Autowired private CarRepository carRepository;
    @Autowired private DriverRepository driverRepository;
    @Autowired private PassengerRepository passengerRepository;
    @Autowired private RideRepository rideRepository;
    @Autowired private RideSolicitationRepository rideSolicitationRepository;

    @BeforeEach
    public void generalSetup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        rideSolicitationRepository.deleteAll();
        rideRepository.deleteAll();
        carRepository.deleteAll();
        driverRepository.deleteAll();
        passengerRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach void tearDown() {
        rideSolicitationRepository.deleteAll();
        rideRepository.deleteAll();
        carRepository.deleteAll();
        driverRepository.deleteAll();
        passengerRepository.deleteAll();
        userRepository.deleteAll();
    }

}
