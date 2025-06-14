package br.ifsp.demo.usecase.car;

import br.ifsp.demo.domain.Car;
import br.ifsp.demo.domain.Cpf;
import br.ifsp.demo.domain.Driver;
import br.ifsp.demo.domain.LicensePlate;
import br.ifsp.demo.exception.CarNotFoundException;
import br.ifsp.demo.exception.DriverNotFoundException;
import br.ifsp.demo.models.response.CarResponseModel;
import br.ifsp.demo.repositories.CarRepository;
import br.ifsp.demo.repositories.DriverRepository;
import br.ifsp.demo.usecase.car.GetCarUseCase;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCarUseCaseTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private GetCarUseCase getCarUseCase;

    private UUID driverId;
    private UUID carId;
    private Driver driver;
    private Car car;

    @BeforeEach
    void setUp() {
        driverId = UUID.randomUUID();
        carId = UUID.randomUUID();

        car = new Car("Toyota","Corolla","Black",5,LicensePlate.parse("ABC1C34"));
        Car car2 = new Car("Volkswagen", "Fusca", "White", 5, LicensePlate.parse("GEK3C45"));

        driver = new Driver("Jose", "Alfredo", "joao@example.com","123123BBdjk", Cpf.of("529.982.247-25"), LocalDate.of(2003, 3,20));
        driverId = driver.getId();

        driver.addCar(car);
        driver.addCar(car2);
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Should return car when driver and car exist and car belongs to driver")
    void shouldReturnCarWhenDriverAndCarExistAndCarBelongsToDriver() {
        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
        when(carRepository.findById(carId)).thenReturn(Optional.of(car));

        CarResponseModel response = getCarUseCase.byId(driverId, carId);

        assertEquals("Toyota", response.brand());
        assertEquals("Corolla", response.model());
        assertEquals("Black", response.color());
        assertEquals(5, response.seats());
        assertEquals("ABC1C34", response.licensePlate());
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Should throw DriverNotFoundException when driver does not exist")
    void shouldThrowDriverNotFoundExceptionWhenDriverDoesNotExist() {
        when(driverRepository.findById(driverId)).thenReturn(Optional.empty());

        assertThrows(DriverNotFoundException.class, () -> getCarUseCase.byId(driverId, carId));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Should throw CarNotFoundException when car does not exist")
    void shouldThrowCarNotFoundExceptionWhenCarDoesNotExist() {
        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
        when(carRepository.findById(carId)).thenReturn(Optional.empty());

        assertThrows(CarNotFoundException.class, () -> getCarUseCase.byId(driverId, carId));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Should throw CarNotFoundException when car does not belong to driver")
    void shouldThrowCarNotFoundExceptionWhenCarDoesNotBelongToDriver() {
        Car otherCar =  new Car("Toyota","Yaris","Black",5,LicensePlate.parse("AAA-1234"));


        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
        when(carRepository.findById(otherCar.getId())).thenReturn(Optional.of(otherCar));

        assertThrows(CarNotFoundException.class, () -> getCarUseCase.byId(driverId, otherCar.getId()));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Should return all cars associated with the driver")
    void shouldReturnAllCarsForDriver() {
        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));

        List<CarResponseModel> cars = getCarUseCase.allCars(driverId);

        assertEquals(2, cars.size());
        assertEquals("Toyota", cars.get(0).brand());
        assertEquals("Volkswagen", cars.get(1).brand());
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Should throw DriverNotFoundException when listing cars if driver does not exist")
    void shouldThrowDriverNotFoundExceptionWhenListingCarsAndDriverDoesNotExist() {
        when(driverRepository.findById(driverId)).thenReturn(Optional.empty());

        assertThrows(DriverNotFoundException.class, () -> getCarUseCase.allCars(driverId));
    }

    @Test
    @Tag("Mutation")
    @Tag("UnitTest")
    @DisplayName("Should not return inactive cars when listing all cars")
    void shouldNotReturnInactiveCarsWhenListingAllCars() {
        car.deactivate();
        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));

        List<CarResponseModel> cars = getCarUseCase.allCars(driverId);

        assertEquals(1, cars.size());
        assertEquals("Volkswagen", cars.get(0).brand());
    }
}
