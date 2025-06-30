package br.ifsp.demo.integration.ui.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;

import br.ifsp.demo.integration.ui.BasePassengerTest;
import br.ifsp.demo.integration.ui.page.UserProfilePage;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UiTest")
public class UserProfileUiTest extends BasePassengerTest {

    @Test
    @DisplayName("Happy Path - Should show name, email, CPF and birth date")
    void shouldShowUserProfileInfo() {
        UserProfilePage page = new UserProfilePage(driver);

        assertThat(page.isNameVisible()).isTrue();
        assertThat(page.isEmailVisible()).isTrue();
        assertThat(page.isCpfVisible()).isTrue();
        assertThat(page.isBirthDateVisible()).isTrue();
    }

    @Test
    @DisplayName("UI Responsiveness - Should show name on mobile layout")
    void shouldRenderNameOnMobileLayout() {
        driver.manage().window().setSize(new Dimension(375, 812));
        UserProfilePage page = new UserProfilePage(driver);

        assertThat(page.isNameVisible()).isTrue();
    }
}