package br.ifsp.demo.integration.ui.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import br.ifsp.demo.integration.ui.BasePageObject;

public class PassengerRideRequestsPage extends BasePageObject {

    private final By title = By.xpath("//*[contains(text(), 'Solicitações de Carona')]");
    private final By rideSolicitations = By.cssSelector(".ride-grid");

    // Novos seletores vindos do RideSolicitationPage
    private final By driverName = By.xpath("//*[contains(text(), 'Nome do Motorista')]/following-sibling::*");
    private final By cancelButton = By.xpath("//button[contains(text(), 'Cancelar')]");
    private final By spinner = By.cssSelector(".chakra-spinner");

    public PassengerRideRequestsPage(WebDriver driver) {
        super(driver);
        waitForVisibility(title);
    }

    public boolean isTitleVisible() {
        return isVisible(title);
    }

    public boolean hasSolicitations() {
        return !driver.findElements(rideSolicitations).isEmpty();
    }

    // Métodos herdados do RideSolicitationPage
    public boolean isDriverNameVisible() {
        return isVisible(driverName);
    }

    public boolean isCancelButtonVisible() {
        return isVisible(cancelButton);
    }

    public boolean isSpinnerVisible() {
        return isVisible(spinner);
    }

    public boolean isResponsiveLayoutVisible() {
        return isVisible(driverName); // reuso
    }
}
