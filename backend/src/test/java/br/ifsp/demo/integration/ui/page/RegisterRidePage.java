package br.ifsp.demo.integration.ui.page;

import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import br.ifsp.demo.integration.ui.BasePageObject;

public class RegisterRidePage extends BasePageObject {

    private static final String REGISTER_RIDE_URL = "http://localhost:5173/dashboard/driver/rides/register";

    private final By startAddressField = By.id("startAddress");
    private final By endAddressField = By.id("endAddress");
    private final By departureTimeField = By.id("departureTime");
    private final By carSelectField = By.id("carSelect");
    private final By submitButton = By.cssSelector("button[type='submit'].auth-button");

    private final By formError = By.cssSelector(".form-error");
    private final By formSuccess = By.cssSelector("div.form-success");

    public RegisterRidePage(WebDriver driver) {
        super(driver);
        driver.get(REGISTER_RIDE_URL);
        waitForVisibility(startAddressField);
    }

    public void fillStartAddress(String startAddress) {
        fillField(startAddressField, startAddress);
    }

    public void fillEndAddress(String endAddress) {
        fillField(endAddressField, endAddress);
    }

    public void fillDepartureTime(String departureTime) {
        WebElement input = driver.findElement(departureTimeField);

        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].setAttribute('value', arguments[1]);" +
            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
            input, departureTime
        );
    }

    public void selectCarByValue(String value) {
        WebElement selectElement = driver.findElement(carSelectField);
        Select select = new Select(selectElement);
        select.selectByValue(value);
    }

    public String selectFirstAvailableCar() {
        WebElement select = driver.findElement(carSelectField);
        List<WebElement> options = select.findElements(By.tagName("option"));
        if (options.size() > 1) {
            WebElement option = options.get(1); // pula o primeiro, que é vazio
            String carId = option.getAttribute("value");
            new Select(select).selectByValue(carId);
            return carId;
        } else {
            throw new NoSuchElementException("No available cars to select.");
        }
    }

    public void printCarOptionsValues() {
        WebElement selectElement = driver.findElement(carSelectField);
        List<WebElement> options = selectElement.findElements(By.tagName("option"));

        for (WebElement option : options) {
            System.out.println("Option value: " + option.getAttribute("value"));
        }
    }

    public void submitForm() {
        System.out.println(">>> Submitting form...");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", driver.findElement(submitButton));
        System.out.println(">>> Submitted.");
    }

    public boolean isFormErrorVisible() {
        return !driver.findElements(formError).isEmpty();
    }

    public String getFormErrorText() {
        if (isFormErrorVisible()) {
            return getElementText(formError);
        }
        return "";
    }

    public boolean isFormSuccessVisible() {
        return !driver.findElements(formSuccess).isEmpty();
    }

    public String getFormSuccessText() {
        if (isFormSuccessVisible()) {
            return getElementText(formSuccess);
        }
        return "";
    }

    public boolean isStartAddressFieldVisible() {
        return isVisible(startAddressField);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public void visit() {
        driver.get(REGISTER_RIDE_URL);
        waitForVisibility(startAddressField);
    }

    public void waitForFormSuccessVisible() {
        waitForVisibility(formSuccess);
    }
}
