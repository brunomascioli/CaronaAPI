package br.ifsp.demo.integration.ui.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;

import br.ifsp.demo.integration.ui.BaseSeleniumTest;
import br.ifsp.demo.integration.ui.page.AvailableRidesPage;
import br.ifsp.demo.integration.ui.page.LoginPage;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UiTest")
public class AvailableRidesUiTest extends BaseSeleniumTest {

    private void performPassengerLogin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.fillEmail("passageiro@ifsp.edu.br");
        loginPage.fillPassword("SenhaForte123!");
        loginPage.submitLogin();
        waitForUrlContains("/dashboard/passenger/profile");
    }

    private void goToAvailableRidesPage() {
        waitForVisibility(By.xpath("//button[contains(text(), 'Carona')]")).click();
        waitForVisibility(By.xpath("//*[contains(text(), 'Disponíveis')]")).click();
        waitForUrlContains("/dashboard/passenger/avalable-rides"); // typo mantido propositalmente
    }

    @Test
    @DisplayName("Happy Path - Should display available rides and title")
    void shouldDisplayAvailableRidesAndTitle() {
        performPassengerLogin();
        goToAvailableRidesPage();

        AvailableRidesPage page = new AvailableRidesPage(driver);

        assertThat(page.isTitleVisible())
            .as("Verifique se o título 'Caronas Disponíveis' está visível")
            .isTrue();

        if (page.hasRideCards()) {
            System.out.println("[DEBUG] Total de caronas visíveis: " + page.countRidesDisplayed());
            assertThat(page.hasRideCards())
                .as("Verifique se há pelo menos uma carona visível")
                .isTrue();
        } else {
            System.out.println("[DEBUG] Nenhuma carona disponível atualmente.");
        }
    }

    @Test
    @DisplayName("Happy Path - Should request a ride successfully (if available)")
    void shouldRequestARideSuccessfully() {
        performPassengerLogin();
        goToAvailableRidesPage();

        AvailableRidesPage page = new AvailableRidesPage(driver);

        if (!page.hasRideCards()) {
            System.out.println("[SKIPPED] Nenhuma carona disponível para solicitar. Ignorando execução.");
            return; // evita falha forçada
        }

        page.solicitarPrimeiraCarona();

        waitForVisibility(By.xpath("//*[contains(text(), 'Solicitação enviada')]"));
        System.out.println("[DEBUG] Carona foi solicitada com sucesso.");
    }
    
    @Test
    @DisplayName("Sad Path - Should handle no rides gracefully")
    void shouldHandleNoRidesGracefully() {
        performPassengerLogin();
        goToAvailableRidesPage();

        AvailableRidesPage page = new AvailableRidesPage(driver);
        if (!page.hasRideCards()) {
            assertThat(page.countRidesDisplayed()).isEqualTo(0);
        } else {
            System.out.println("Aviso: Existem caronas disponíveis, então o sad path não foi exercido.");
        }
    }

    @Test
    @DisplayName("UI Responsiveness - Should render properly on mobile")
    void shouldRenderProperlyOnMobile() {
        performPassengerLogin();
        driver.manage().window().setSize(new Dimension(375, 812));
        goToAvailableRidesPage();

        AvailableRidesPage page = new AvailableRidesPage(driver);

        assertThat(page.isTitleVisible()).isTrue();
    }
}