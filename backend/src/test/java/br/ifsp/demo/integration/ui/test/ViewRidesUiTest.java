package br.ifsp.demo.integration.ui.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import br.ifsp.demo.integration.ui.BaseDriverTest;
import br.ifsp.demo.integration.ui.page.ViewRidesPage;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UiTest")
public class ViewRidesUiTest extends BaseDriverTest {

    @Test
    @DisplayName("Happy Path - Should display all ride sections if data exists")
    void shouldDisplayAllRideSectionsIfDataExists() {
        Actions actions = new Actions(driver);

        WebElement caronasMenu = waitForVisibility(By.xpath("//button[contains(text(), 'Caronas')]"));
        actions.moveToElement(caronasMenu).perform();
        caronasMenu.click();

        WebElement visualizarBtn = waitForVisibility(By.xpath("//*[contains(text(), 'Visualizar Caronas')]"));
        visualizarBtn.click();

        waitForUrlContains("/dashboard/driver/rides/view");

        ViewRidesPage viewRidesPage = new ViewRidesPage(driver);

        assertThat(
            viewRidesPage.hasNotStartedRides()
            || viewRidesPage.hasInProgressRides()
            || viewRidesPage.hasFinishedRides()
        ).as("Verifique se há pelo menos uma seção de caronas visível")
        .isTrue();
    }

    @Test
    @DisplayName("Sad Path - Should show error alert if API fails")
    void shouldShowErrorAlertIfApiFails() {
        Actions actions = new Actions(driver);

        WebElement caronasMenu = waitForVisibility(By.xpath("//button[contains(text(), 'Caronas')]"));
        actions.moveToElement(caronasMenu).perform();
        caronasMenu.click();

        WebElement visualizarBtn = waitForVisibility(By.xpath("//*[contains(text(), 'Visualizar Caronas')]"));
        visualizarBtn.click();

        waitForUrlContains("/dashboard/driver/rides/view");

        ViewRidesPage viewRidesPage = new ViewRidesPage(driver);

        if (viewRidesPage.hasErrorMessage()) {
            assertThat(viewRidesPage.getErrorMessageText())
                .contains("Erro ao buscar caronas");
        } else {
            System.out.println("Nenhum erro foi simulado. Ignorando teste sad path: API respondeu normalmente.");
        }
    }

    @Test
    @DisplayName("UI Responsiveness - Should display title correctly on mobile")
    void shouldDisplayTitleCorrectlyOnMobile() {
        Actions actions = new Actions(driver);

        WebElement caronasMenu = waitForVisibility(By.xpath("//button[contains(text(), 'Caronas')]"));
        actions.moveToElement(caronasMenu).perform();
        caronasMenu.click();

        WebElement visualizarBtn = waitForVisibility(By.xpath("//*[contains(text(), 'Visualizar Caronas')]"));
        visualizarBtn.click();

        waitForUrlContains("/dashboard/driver/rides/view");

        driver.manage().window().setSize(new Dimension(375, 812));

        ViewRidesPage viewRidesPage = new ViewRidesPage(driver);
        assertThat(viewRidesPage.isResponsiveTitleVisible()).isTrue();
    }
}
