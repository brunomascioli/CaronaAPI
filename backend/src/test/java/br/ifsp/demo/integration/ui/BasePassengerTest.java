package br.ifsp.demo.integration.ui;

import br.ifsp.demo.integration.ui.page.LoginPage;
import org.junit.jupiter.api.Tag;

@Tag("UiTest")
public abstract class BasePassengerTest extends BaseSeleniumTest {

    @Override
    protected void setInitialPage() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.doLogin("passageiro@ifsp.edu.br", "SenhaForte123!", "/dashboard/passenger/profile");
    }
}
