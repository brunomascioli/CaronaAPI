package br.ifsp.demo.integration.ui.test;

import br.ifsp.demo.integration.ui.BaseSeleniumTest;
import br.ifsp.demo.integration.ui.page.RegisterPassengerPage;
import br.ifsp.demo.integration.ui.util.FakeDataFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

@Tag("UiTest")
public class RegisterPassengerUiTest extends BaseSeleniumTest {

    @Override
    protected void setInitialPage() {
        new RegisterPassengerPage(driver);
    }

    @Test
    @DisplayName("Setup - Should ensure passenger account with fixed email exists")
    void shouldEnsurePassengerAccountExists() {
        RegisterPassengerPage registerPassengerPage = new RegisterPassengerPage(driver);

        String name = "João";
        String lastname = "Passenger";
        String email = "passageiro@ifsp.edu.br";
        String cpf = "465.286.180-03";
        String birthDate = "2000-05-10";
        String password = "SenhaForte123!";

        registerPassengerPage.fillName(name);
        registerPassengerPage.fillLastname(lastname);
        registerPassengerPage.fillEmail(email);
        registerPassengerPage.fillCpf(cpf);
        registerPassengerPage.fillBirthDate(birthDate);
        registerPassengerPage.fillPassword(password);
        registerPassengerPage.fillConfirmPassword(password);

        registerPassengerPage.submitForm();

        try {
            new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
            new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.urlContains("/login"));
        } catch (Exception e) {
            assertThat(driver.getCurrentUrl()).contains("/login");
        }
    }


    @Test
    @DisplayName("Happy Path - Should register passenger with valid data")
    void shouldRegisterPassengerWithValidData() {
        RegisterPassengerPage registerPassengerPage = new RegisterPassengerPage(driver);

        String name = FakeDataFactory.randomName();
        String lastname = FakeDataFactory.randomLastName();
        String email = FakeDataFactory.randomEmail();
        String cpf = FakeDataFactory.generateValidCpfFromList();
        String birthDate = FakeDataFactory.randomBirthDate();
        String password = FakeDataFactory.strongPassword();

        registerPassengerPage.fillName(name);
        registerPassengerPage.fillLastname(lastname);
        registerPassengerPage.fillEmail(email);
        registerPassengerPage.fillCpf(cpf);
        registerPassengerPage.fillBirthDate(birthDate);
        registerPassengerPage.fillPassword(password);
        registerPassengerPage.fillConfirmPassword(password);

        registerPassengerPage.submitForm();

        new WebDriverWait(driver, Duration.ofSeconds(3))
            .until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(ExpectedConditions.urlContains("/login"));

        assertThat(driver.getCurrentUrl()).contains("/login");
    }

    @Test
    @DisplayName("Sad Path - Should stay on register-passenger page with empty form")
    void shouldStayOnRegisterPassengerPageWithEmptyForm() {
        RegisterPassengerPage registerPassengerPage = new RegisterPassengerPage(driver);

        registerPassengerPage.submitForm();

        assertThat(driver.getCurrentUrl()).endsWith("/register-passenger");
        assertThat(registerPassengerPage.isNameFieldVisible()).isTrue();
    }

    @Test
    @DisplayName("UI Responsiveness - Should display Register Passenger page correctly on mobile size")
    void shouldDisplayRegisterPassengerPageCorrectlyOnMobile() {
        driver.manage().window().setSize(new Dimension(375, 812));

        RegisterPassengerPage registerPassengerPage = new RegisterPassengerPage(driver);

        assertThat(registerPassengerPage.isNameFieldVisible()).isTrue();
    }
}
