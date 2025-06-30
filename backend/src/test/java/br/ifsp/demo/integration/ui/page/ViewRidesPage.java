package br.ifsp.demo.integration.ui.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import br.ifsp.demo.integration.ui.BasePageObject;

public class ViewRidesPage extends BasePageObject {

    private static final String VIEW_RIDES_URL = "http://localhost:5173/dashboard/driver/rides/view";

    private final By notStartedTitle = By.xpath("//*[contains(text(),'Caronas não iniciadas')]");
    private final By inProgressTitle = By.xpath("//*[contains(text(),'Em andamento')]");
    private final By finishedTitle = By.xpath("//*[contains(text(),'Caronas encerradas')]");
    private final By errorAlert = By.cssSelector(".chakra-alert");

    public ViewRidesPage(WebDriver driver) {
        super(driver);
    }

    public void visit() {
        driver.get(VIEW_RIDES_URL);
        waitForAnySection();
    }

    private void waitForAnySection() {
        waitUntilAnyVisible(notStartedTitle, inProgressTitle, finishedTitle, errorAlert);
    }

    public boolean hasNotStartedRides() {
        return isVisible(notStartedTitle);
    }

    public boolean hasInProgressRides() {
        return isVisible(inProgressTitle);
    }

    public boolean hasFinishedRides() {
        return isVisible(finishedTitle);
    }

    public boolean hasErrorMessage() {
        return isVisible(errorAlert);
    }

    public String getErrorMessageText() {
        return getElementText(errorAlert);
    }

    public boolean isResponsiveTitleVisible() {
        return isVisible(notStartedTitle);
    }
}