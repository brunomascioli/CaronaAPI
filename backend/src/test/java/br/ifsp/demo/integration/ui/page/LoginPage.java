package br.ifsp.demo.integration.ui.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import br.ifsp.demo.integration.ui.BasePageObject;

public class LoginPage extends BasePageObject {

    private static final String LOGIN_URL = "http://localhost:5173/login";

    private final By emailField = By.name("username");
    private final By passwordField = By.name("password");
    private final By submitButton = By.cssSelector("button[type='submit']");

    public LoginPage(WebDriver driver) {
        super(driver);
        driver.get(LOGIN_URL);
        waitForVisibility(emailField);
    }

    public void fillEmail(String email) {
        fillField(emailField, email);
    }

    public void fillPassword(String password) {
        fillField(passwordField, password);
    }

    public void submitLogin() {
        clickWhenClickable(submitButton);
    }

    public void doLogin(String email, String password, String redirectPathAfterLogin) {
        fillEmail(email);
        fillPassword(password);
        submitLogin();
        waitForUrlContains(redirectPathAfterLogin);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    @Override
    public String getPageTitle() {
        return super.getPageTitle();
    }

    public boolean isEmailFieldVisible() {
        return isVisible(emailField);
    }
}
