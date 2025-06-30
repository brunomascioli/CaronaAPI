package br.ifsp.demo.integration.ui.test;

import br.ifsp.demo.integration.ui.BaseSeleniumTest;
import br.ifsp.demo.integration.ui.page.LoginPage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;

@Tag("UiTest")
public class LoginDriverUiTest extends BaseSeleniumTest {

    @Test
    @DisplayName("Happy Path - Should login with valid Driver credentials")
    void shouldLoginWithValidDriverCredentials() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.fillEmail("motorista@ifsp.edu.br");
        loginPage.fillPassword("SenhaForte123!");
        loginPage.submitLogin();

        waitForUrlContains("/dashboard/driver/profile");
        assertTrue(driver.getCurrentUrl().contains("/dashboard/driver/profile"));
    }

        @Test
    @DisplayName("Sad Path - Should stay on login page with invalid credentials")
    void shouldStayOnLoginPageWithInvalidCredentials() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.fillEmail("invalid@example.com");
        loginPage.fillPassword("wrongpassword");
        loginPage.submitLogin();

        assertThat(driver.getCurrentUrl()).endsWith("/login");
        assertThat(loginPage.getPageTitle()).isEqualTo("Vite + React");
    }

    @Test
    @DisplayName("UI Responsiveness - Should display login page correctly on mobile size")
    void shouldDisplayLoginPageCorrectlyOnMobile() {
        driver.manage().window().setSize(new Dimension(375, 812));

        LoginPage loginPage = new LoginPage(driver);

        assertThat(loginPage.isEmailFieldVisible()).isTrue();
    }
}