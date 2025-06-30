package br.ifsp.demo.integration.ui.test;

import static org.assertj.core.api.Assertions.assertThat;

import br.ifsp.demo.integration.ui.BaseSeleniumTest;
import br.ifsp.demo.integration.ui.page.RegisterDriverPage;
import br.ifsp.demo.integration.ui.util.FakeDataFactory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

@Tag("UiTest")
public class RegisterDriverUiTest extends BaseSeleniumTest {

    @Override
    protected void setInitialPage() {
        new RegisterDriverPage(driver);
    }

    @Test
    @DisplayName("Setup - Should ensure driver account with fixed email exists")
    void shouldEnsureDriverAccountExists() {
        RegisterDriverPage registerDriverPage = new RegisterDriverPage(driver);

        String name = "Sophie";
        String lastname = "Driver";
        String email = "motorista@ifsp.edu.br";
        String cpf = "812.976.140-89";
        String birthDate = "2000-12-09";
        String password = "SenhaForte123!";

        registerDriverPage.fillName(name);
        registerDriverPage.fillLastname(lastname);
        registerDriverPage.fillEmail(email);
        registerDriverPage.fillCpf(cpf);
        registerDriverPage.fillBirthDate(birthDate);
        registerDriverPage.fillPassword(password);
        registerDriverPage.fillConfirmPassword(password);

        registerDriverPage.submitForm();

        try {
            new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
            new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.urlContains("/login"));
        } catch (Exception e) {
            assertThat(driver.getCurrentUrl()).contains("/login");
        }
    }

    @Test
    @DisplayName("Happy Path - Should register driver with valid data")
    void shouldRegisterDriverWithValidData() {
        RegisterDriverPage registerDriverPage = new RegisterDriverPage(driver);

        String name = FakeDataFactory.randomName();
        String lastname = FakeDataFactory.randomLastName();
        String email = FakeDataFactory.randomEmail();
        String cpf = FakeDataFactory.generateValidCpfFromList();
        String birthDate = FakeDataFactory.randomBirthDate();
        String password = FakeDataFactory.strongPassword();

        registerDriverPage.fillName(name);
        registerDriverPage.fillLastname(lastname);
        registerDriverPage.fillEmail(email);
        registerDriverPage.fillCpf(cpf);
        registerDriverPage.fillBirthDate(birthDate);
        registerDriverPage.fillPassword(password);
        registerDriverPage.fillConfirmPassword(password);

        registerDriverPage.submitForm();

        try {
            new WebDriverWait(driver, Duration.ofSeconds(3))
                .until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();

            System.out.println("[DEBUG] URL após aceitar alerta: " + driver.getCurrentUrl());
            System.out.println("[DEBUG] Título da página: " + driver.getTitle());
            System.out.println("[DEBUG] Data de nascimento gerada: " + birthDate);
            
            new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlContains("/login"));

            assertThat(driver.getCurrentUrl()).contains("/login");

        } catch (Exception e) {
            takeScreenshot("register-driver-failure");
            throw e;
        }
    }

    @Test
    @DisplayName("Sad Path - Should stay on register-driver page with empty form")
    void shouldStayOnRegisterDriverPageWithEmptyForm() {
        RegisterDriverPage registerDriverPage = new RegisterDriverPage(driver);
        registerDriverPage.submitForm();
        assertThat(driver.getCurrentUrl()).endsWith("/register-driver");
        assertThat(registerDriverPage.isNameFieldVisible()).isTrue();
    }

    @Test
    @DisplayName("UI Responsiveness - Should display Register Driver page correctly on mobile size")
    void shouldDisplayRegisterDriverPageCorrectlyOnMobile() {
        driver.manage().window().setSize(new Dimension(375, 812));
        RegisterDriverPage registerDriverPage = new RegisterDriverPage(driver);
        assertThat(registerDriverPage.isNameFieldVisible()).isTrue();
    }

    private void takeScreenshot(String fileName) {
        String path = "src/test/java/br/ifsp/demo/integration/ui/screenshots/";
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File destFile = new File(path + fileName + ".png");
        try {
            FileHandler.createDir(new File(path));
            FileHandler.copy(screenshot, destFile);
            System.out.println("[DEBUG] Screenshot salva em: " + destFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("[ERROR] Falha ao salvar screenshot: " + e.getMessage());
        }
    }
}