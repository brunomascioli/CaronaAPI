package br.ifsp.demo.integration.ui.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import br.ifsp.demo.integration.ui.BasePageObject;

public class UserProfilePage extends BasePageObject {

    private static final String PROFILE_URL = "http://localhost:5173/dashboard/passenger/profile";

    private final By nameHeading = By.cssSelector(".passenger-profile-box h2");
    private final By emailText = By.xpath("//*[contains(@class, 'passenger-profile-box')]//*[contains(text(), '@')]");
    private final By cpfText = By.xpath("//*[contains(text(),'CPF:')]");
    private final By birthDateText = By.xpath("//*[contains(text(),'Data de Nascimento:')]");

    public UserProfilePage(WebDriver driver) {
        super(driver);
        waitForVisibility(nameHeading);
    }

    public boolean isNameVisible() {
        return isVisible(nameHeading);
    }

    public boolean isEmailVisible() {
        return !driver.findElements(emailText).isEmpty();
    }

    public boolean isCpfVisible() {
        return !driver.findElements(cpfText).isEmpty();
    }

    public boolean isBirthDateVisible() {
        return !driver.findElements(birthDateText).isEmpty();
    }
}
