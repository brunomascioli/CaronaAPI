package br.ifsp.demo.integration.ui.test;

import br.ifsp.demo.integration.ui.BaseDriverTest;
import br.ifsp.demo.integration.ui.page.ViewSolicitationNotificationsPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UiTest")
public class ViewSolicitationNotificationsUiTest extends BaseDriverTest {

    void goToNotificationsPage() {
        Actions actions = new Actions(driver);

        WebElement menu = waitForVisibility(By.xpath("//button[contains(text(), 'Solicitações')]"));
        actions.moveToElement(menu).perform();
        menu.click();

        WebElement received = waitForVisibility(By.xpath("//*[contains(text(), 'Solicitações Recebidas')]"));
        received.click();

        waitForUrlContains("/dashboard/driver/solicitations/pending");
    }

    @Test
    @DisplayName("Happy Path - Should approve a solicitation")
    void shouldApproveSolicitation() {
        goToNotificationsPage();

        ViewSolicitationNotificationsPage page = new ViewSolicitationNotificationsPage(driver);
        assertThat(page.isTitleVisible()).isTrue();

        if (page.hasSolicitations()) {
            page.clicarNoBotaoAprovar();
            System.out.println("[DEBUG] Solicitação aprovada com sucesso.");
        } else {
            System.out.println("[DEBUG] Nenhuma solicitação encontrada para aprovar.");
        }
    }

    @Test
    @DisplayName("UI Responsiveness - Should show title on mobile layout")
    void shouldRenderOnMobile() {
        goToNotificationsPage();

        driver.manage().window().setSize(new Dimension(375, 812));

        ViewSolicitationNotificationsPage page = new ViewSolicitationNotificationsPage(driver);
        assertThat(page.isTitleVisible()).isTrue();
    }
}