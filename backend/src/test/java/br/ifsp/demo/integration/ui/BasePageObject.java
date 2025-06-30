package br.ifsp.demo.integration.ui;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BasePageObject {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    public BasePageObject(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // default wait de 10s
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    protected WebElement waitForVisibility(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForPresence(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    protected boolean isVisible(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected void clickWhenClickable(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void fillField(By locator, String text) {
        WebElement field = waitForVisibility(locator);
        field.clear();
        field.sendKeys(text);
    }

    protected void fillWithoutClearing(By locator, String text) {
        WebElement field = waitForVisibility(locator);
        field.sendKeys(text);
    }

    protected String getElementText(By locator) {
        return waitForVisibility(locator).getText();
    }

    protected void waitForInvisibility(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    protected void waitForUrlContains(String partialUrl) {
        wait.until(ExpectedConditions.urlContains(partialUrl));
    }

    protected void waitUntilAnyVisible(By... locators) {
        wait.until(driver -> {
            for (By locator : locators) {
                if (!driver.findElements(locator).isEmpty() && driver.findElement(locator).isDisplayed()) {
                    return true;
                }
            }
            return false;
        });
    }
}
