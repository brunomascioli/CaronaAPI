package br.ifsp.demo.integration.suits;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import br.ifsp.demo.integration.ui.test.*;

@Suite
@SelectClasses({
    RegisterDriverUiTest.class,
    RegisterPassengerUiTest.class,
    LoginDriverUiTest.class,
    LoginPassengerUiTest.class,
    RegisterCarUiTest.class,
    RegisterRideUiTest.class,
    AvailableRidesUiTest.class,
    ViewRidesUiTest.class,
    UserProfileUiTest.class,
    PassengerRideRequestsUiTest.class,
    ViewSolicitationNotificationsUiTest.class,
    PassengerRidesUiTest.class
})

@IncludeTags("UiTest")
public class AllUiTests {
}