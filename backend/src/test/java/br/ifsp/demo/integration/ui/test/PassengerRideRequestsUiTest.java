package br.ifsp.demo.integration.ui.test;

import br.ifsp.demo.integration.ui.BasePassengerTest;
import br.ifsp.demo.integration.ui.page.PassengerRideRequestsPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UiTest")
public class PassengerRideRequestsUiTest extends BasePassengerTest {

    void acessarSolicitacoesEnviadas() {
        waitForVisibility(By.xpath("//button[contains(text(), 'Carona')]")).click();
        waitForVisibility(By.xpath("//*[contains(text(), 'Solicitações Enviadas')]")).click();
        waitForUrlContains("/dashboard/passenger/ride-requests");
    }

    @Test
    @DisplayName("Happy Path - Should display ride requests and title")
    void shouldDisplayRideRequestsAndTitle() {
        acessarSolicitacoesEnviadas();

        PassengerRideRequestsPage page = new PassengerRideRequestsPage(driver);

        assertThat(page.isTitleVisible()).isTrue();

        if (page.hasSolicitations()) {
            System.out.println("[DEBUG] Solicitações visíveis.");
        } else {
            System.out.println("[DEBUG] Nenhuma solicitação encontrada.");
        }
    }

    @Test
    @DisplayName("UI Responsiveness - Should show title on mobile layout")
    void shouldRenderOnMobile() {
        driver.manage().window().setSize(new Dimension(375, 812));
        acessarSolicitacoesEnviadas();

        PassengerRideRequestsPage page = new PassengerRideRequestsPage(driver);

        assertThat(page.isTitleVisible()).isTrue();
    }

    @Test
    @DisplayName("Happy Path - Should show driver name and cancel button after request")
    void shouldShowDriverNameAndCancelButtonAfterRequest() {
        acessarSolicitacoesEnviadas();

        PassengerRideRequestsPage page = new PassengerRideRequestsPage(driver);

        if (page.hasSolicitations()) {
            assertThat(page.isDriverNameVisible()).isTrue();
            assertThat(page.isCancelButtonVisible()).isTrue();
        } else {
            System.out.println("[DEBUG] Nenhuma solicitação encontrada para validar nome ou botão.");
        }
    }

    @Test
    @DisplayName("Sad Path - Should show spinner if loading")
    void shouldShowSpinnerIfLoading() {
        acessarSolicitacoesEnviadas();

        PassengerRideRequestsPage page = new PassengerRideRequestsPage(driver);
        if (page.isSpinnerVisible()) {
            assertThat(page.isCancelButtonVisible()).isFalse();
        } else {
            System.out.println("[DEBUG] Nenhum spinner encontrado.");
        }
    }
}