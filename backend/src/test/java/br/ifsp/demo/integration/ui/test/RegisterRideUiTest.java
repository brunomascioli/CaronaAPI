package br.ifsp.demo.integration.ui.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoAlertPresentException;

import br.ifsp.demo.integration.ui.BaseDriverTest;
import br.ifsp.demo.integration.ui.page.LoginPage;
import br.ifsp.demo.integration.ui.page.RegisterRidePage;
import br.ifsp.demo.integration.ui.util.FakeDataFactory;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Tag("UiTest")
public class RegisterRideUiTest extends BaseDriverTest {

    @Test
    @DisplayName("Happy Path - Should register ride with valid data")
    void shouldRegisterRideWithValidData() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.fillEmail("motorista@ifsp.edu.br");
        loginPage.fillPassword("SenhaForte123!");
        loginPage.submitLogin();

        waitForUrlContains("/dashboard/driver/profile");

        RegisterRidePage registerRidePage = new RegisterRidePage(driver);
        registerRidePage.visit();

        String startAddress = FakeDataFactory.randomAddress();
        String endAddress = FakeDataFactory.randomAddress();
        String formattedFutureDateTime = LocalDateTime.now()
            .plusDays(1)
            .withHour(10).withMinute(0)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));

        System.out.println(">>> Data para setar: " + formattedFutureDateTime);
        System.out.println(">>> Campo após set: " + driver.findElement(By.id("departureTime")).getAttribute("value"));

        registerRidePage.fillStartAddress(startAddress);
        registerRidePage.fillEndAddress(endAddress);
        registerRidePage.fillDepartureTime(formattedFutureDateTime);

        System.out.println("Departure field value: " + driver.findElement(By.id("departureTime")).getAttribute("value"));
        registerRidePage.printCarOptionsValues();
        String selectedCarId = registerRidePage.selectFirstAvailableCar();

        System.out.println("Start: " + startAddress);
        System.out.println("End: " + endAddress);
        System.out.println("Date: " + formattedFutureDateTime);
        System.out.println("Car ID: " + selectedCarId);

        registerRidePage.submitForm();
        registerRidePage.waitForFormSuccessVisible();

        System.out.println("--- DEBUG ---");
        System.out.println("Current URL: " + driver.getCurrentUrl());
        System.out.println("Form success visible? " + registerRidePage.isFormSuccessVisible());
        System.out.println("Form success text: " + registerRidePage.getFormSuccessText());
        System.out.println("--- END DEBUG ---");

        assertThat(registerRidePage.isFormSuccessVisible()).isTrue();
        assertThat(registerRidePage.getFormSuccessText()).contains("Ride registered successfully!");
    }

    @Test
    @DisplayName("Sad Path - Should show error when submitting empty form")
    void shouldShowErrorWhenSubmittingEmptyForm() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.fillEmail("motorista@ifsp.edu.br");
        loginPage.fillPassword("SenhaForte123!");
        loginPage.submitLogin();

        waitForUrlContains("/dashboard/driver/profile");

        RegisterRidePage registerRidePage = new RegisterRidePage(driver);
        registerRidePage.visit();

        registerRidePage.submitForm();

        assertThat(registerRidePage.getCurrentUrl()).contains("/dashboard/driver/rides/register");

        assertThat(driver.findElement(By.id("startAddress")).getAttribute("value")).isEmpty();
        assertThat(driver.findElement(By.id("endAddress")).getAttribute("value")).isEmpty();
    }
    
    @Test
    @DisplayName("UI Responsiveness - Should display Register Ride page correctly on mobile size")
    void shouldDisplayRegisterRidePageCorrectlyOnMobile() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.fillEmail("motorista@ifsp.edu.br");
        loginPage.fillPassword("SenhaForte123!");
        loginPage.submitLogin();

        try {
            driver.switchTo().alert().accept();
        } catch (NoAlertPresentException ignored) {}

        waitForUrlContains("/dashboard/driver/profile");

        driver.manage().window().setSize(new Dimension(375, 812));

        RegisterRidePage registerRidePage = new RegisterRidePage(driver);
        registerRidePage.visit();

        assertThat(registerRidePage.isStartAddressFieldVisible()).isTrue();
    }
}
