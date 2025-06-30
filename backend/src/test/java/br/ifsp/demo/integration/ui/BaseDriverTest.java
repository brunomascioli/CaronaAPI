package br.ifsp.demo.integration.ui;

import br.ifsp.demo.integration.ui.page.LoginPage;
import org.junit.jupiter.api.Tag;

@Tag("UiTest")
public abstract class BaseDriverTest extends BaseSeleniumTest {

    @Override
    protected void setInitialPage() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.fillEmail("motorista@ifsp.edu.br");
        loginPage.fillPassword("SenhaForte123!");
        loginPage.submitLogin();
        waitForUrlContains("/dashboard/driver/profile");
    }
}