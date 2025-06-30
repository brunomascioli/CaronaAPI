package br.ifsp.demo.integration.ui.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import br.ifsp.demo.integration.ui.BasePageObject;

public class AvailableRidesPage extends BasePageObject {

    private static final String AVAILABLE_RIDES_URL = "http://localhost:5173/dashboard/passenger/avalable-rides";

    private final By pageTitle = By.xpath("//*[contains(text(), 'Caronas Disponíveis')]");
    private final By rideCards = By.xpath("//button[contains(text(), 'Solicitar')]/ancestor::div[contains(@class, 'chakra-card__root')]");
    private final By solicitarButton = By.xpath("//button[contains(text(), 'Solicitar')]");

    public AvailableRidesPage(WebDriver driver) {
        super(driver);
    }
    
    public void visit() {
        driver.get(AVAILABLE_RIDES_URL);
        waitForVisibility(pageTitle);
    }

    public void solicitarPrimeiraCarona() {
        waitForVisibility(solicitarButton);
        clickWhenClickable(solicitarButton);
    }

    public boolean isTitleVisible() {
        return isVisible(pageTitle);
    }

    public boolean hasRideCards() {
        return !driver.findElements(rideCards).isEmpty();
    }

    public int countRidesDisplayed() {
        return driver.findElements(rideCards).size();
    }
}
