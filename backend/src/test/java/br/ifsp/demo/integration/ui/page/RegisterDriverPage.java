package br.ifsp.demo.integration.ui.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import br.ifsp.demo.integration.ui.BasePageObject;

public class RegisterDriverPage extends BasePageObject {

    private static final String REGISTER_DRIVER_URL = "http://localhost:5173/register-driver";

    private final By nameField = By.name("name");
    private final By lastnameField = By.name("lastname");
    private final By emailField = By.name("email");
    private final By cpfField = By.name("cpf");
    private final By birthDateField = By.name("birthDate");
    private final By passwordField = By.name("password");
    private final By confirmPasswordField = By.name("confirmPassword");
    private final By submitButton = By.cssSelector("button[type='submit']");

    public RegisterDriverPage(WebDriver driver) {
        super(driver);
        driver.get(REGISTER_DRIVER_URL);
        waitForVisibility(nameField);
    }

    public void fillName(String name) {
        fillField(nameField, name);
    }

    public void fillLastname(String lastname) {
        fillField(lastnameField, lastname);
    }

    public void fillEmail(String email) {
        fillField(emailField, email);
    }

    public void fillCpf(String cpf) {
        fillField(cpfField, cpf);
    }

    public void fillBirthDate(String birthDate) {
        fillField(birthDateField, birthDate);
    }

    public void fillPassword(String password) {
        fillField(passwordField, password);
    }

    public void fillConfirmPassword(String confirmPassword) {
        fillField(confirmPasswordField, confirmPassword);
    }

    public void submitForm() {
        clickWhenClickable(submitButton);
    }

    public boolean isNameFieldVisible() {
        return driver.findElement(nameField).isDisplayed();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
