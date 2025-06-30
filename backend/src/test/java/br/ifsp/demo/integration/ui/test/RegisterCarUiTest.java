package br.ifsp.demo.integration.ui.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;

import br.ifsp.demo.integration.ui.BaseSeleniumTest;
import br.ifsp.demo.integration.ui.page.LoginPage;
import br.ifsp.demo.integration.ui.page.RegisterCarPage;
import br.ifsp.demo.integration.ui.util.FakeDataFactory;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UiTest")
public class RegisterCarUiTest extends BaseSeleniumTest {

    @Test
    @DisplayName("Happy Path - Should register car with valid data")
    void shouldRegisterCarWithValidData() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.fillEmail("motorista@ifsp.edu.br");
        loginPage.fillPassword("SenhaForte123!");
        loginPage.submitLogin();

        waitForUrlContains("/dashboard/driver/profile");

        RegisterCarPage registerCarPage = new RegisterCarPage(driver);
        registerCarPage.visit();

        registerCarPage.fillBrand(FakeDataFactory.randomCarBrand());
        registerCarPage.fillModel(FakeDataFactory.randomCarModel());
        registerCarPage.fillColor(FakeDataFactory.randomColor());
        registerCarPage.fillSeats(String.valueOf(FakeDataFactory.randomSeats()));
        registerCarPage.fillLicensePlate(FakeDataFactory.randomPlate());

        registerCarPage.submitForm();

        waitForUrlContains("/dashboard/driver/cars/view");
        assertThat(driver.getCurrentUrl()).contains("/dashboard/driver/cars/view");
    }

    @Test
    @DisplayName("Sad Path - Should show error when submitting empty form")
    void shouldShowErrorWhenSubmittingEmptyForm() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.fillEmail("motorista@ifsp.edu.br");
        loginPage.fillPassword("SenhaForte123!");
        loginPage.submitLogin();

        waitForUrlContains("/dashboard/driver/profile");

        RegisterCarPage registerCarPage = new RegisterCarPage(driver);
        registerCarPage.visit();

        registerCarPage.submitForm();
        assertThat(registerCarPage.isAnyFieldErrorVisible()).isTrue();
    }

    @Test
    @DisplayName("UI Responsiveness - Should display Register Car page correctly on mobile size")
    void shouldDisplayRegisterCarPageCorrectlyOnMobile() {
        driver.manage().window().setSize(new Dimension(375, 812));

        LoginPage loginPage = new LoginPage(driver);
        loginPage.fillEmail("motorista@ifsp.edu.br");
        loginPage.fillPassword("SenhaForte123!");
        loginPage.submitLogin();

        waitForUrlContains("/dashboard/driver/profile");

        RegisterCarPage registerCarPage = new RegisterCarPage(driver);
        registerCarPage.visit();

        assertThat(registerCarPage.isBrandFieldVisible()).isTrue();
    }
}