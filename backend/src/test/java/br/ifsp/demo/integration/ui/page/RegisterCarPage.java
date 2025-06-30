package br.ifsp.demo.integration.ui.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import br.ifsp.demo.integration.ui.BasePageObject;

public class RegisterCarPage extends BasePageObject {

    private static final String REGISTER_CAR_URL = "http://localhost:5173/dashboard/driver/cars/register";

    private final By brandField = By.id("brand");
    private final By modelField = By.id("model");
    private final By colorField = By.id("color");
    private final By seatsField = By.id("seats");
    private final By licensePlateField = By.id("licensePlate");
    private final By submitButton = By.cssSelector("button.auth-button");

    private final By formError = By.cssSelector("div.form-error");
    private final By fieldError = By.cssSelector("span.form-error");

    public RegisterCarPage(WebDriver driver) {
        super(driver);
    }

    public void visit() {
        driver.get(REGISTER_CAR_URL);
        waitForVisibility(brandField);
    }

    public void fillBrand(String brand) {
        fillField(brandField, brand);
    }

    public void fillModel(String model) {
        fillField(modelField, model);
    }

    public void fillColor(String color) {
        fillField(colorField, color);
    }

    public void fillSeats(String seats) {
        fillField(seatsField, seats);
    }

    public void fillLicensePlate(String licensePlate) {
        fillField(licensePlateField, licensePlate);
    }

    public void submitForm() {
        clickWhenClickable(submitButton);
    }

    public boolean isGeneralFormErrorVisible() {
        return !driver.findElements(formError).isEmpty();
    }

    public boolean isAnyFieldErrorVisible() {
        return !driver.findElements(fieldError).isEmpty();
    }

    public String getGeneralFormErrorText() {
        if (isGeneralFormErrorVisible()) {
            return getElementText(formError);
        }
        return "";
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public boolean isBrandFieldVisible() {
        return isVisible(brandField);
    }
}
