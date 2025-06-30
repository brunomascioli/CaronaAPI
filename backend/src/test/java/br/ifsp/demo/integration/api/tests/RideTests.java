package br.ifsp.demo.integration.api.tests;

import br.ifsp.demo.integration.api.utils.*;
import br.ifsp.demo.models.request.CarRequestModel;
import br.ifsp.demo.models.request.RideRequestModel;
import br.ifsp.demo.security.auth.AuthRequest;
import io.restassured.filter.log.LogDetail;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class RideTests extends BaseApiIntegrationTest {

    private String authenticationTokenDriver;
    private String authenticationTokenPassenger;
    private String carId;


    private void setAuthenticationTokenDriver(){
        final RegisterUserRequest user = DriverEntityBuilder.createDriverByEmail("password", "driver@email.com");
        given().contentType("application/json").port(port).body(user)
                .when().post("/api/v1/register")
                .then().log().ifValidationFails(LogDetail.BODY);

        final AuthRequest authRequest = new AuthRequest("driver@email.com","password");
        Response loginResponse = given().contentType("application/json").port(port).body(authRequest)
                .when().post("/api/v1/authenticate")
                .then().log().ifValidationFails(LogDetail.BODY).extract().response();
        this.authenticationTokenDriver = loginResponse.jsonPath().getString("token");
    }

    private void setAuthenticationTokenPassenger(){
        final RegisterUserRequest user = PassengerEntityBuilder.createPassengerByEmail("password", "passenger@email.com");
        given().contentType("application/json").port(port).body(user)
                .when().post("/api/v1/register")
                .then().log().ifValidationFails(LogDetail.BODY);

        final AuthRequest authRequest = new AuthRequest("passenger@email.com","password");
        Response loginResponse = given().contentType("application/json").port(port).body(authRequest)
                .when().post("/api/v1/authenticate")
                .then().log().ifValidationFails(LogDetail.BODY).extract().response();
        this.authenticationTokenPassenger = loginResponse.jsonPath().getString("token");
    }

    private void setCarId(){
        final CarRequestModel car = CarEntityBuilder.createRandomCar();
        Response response = given().header("Authorization", "Bearer " + authenticationTokenDriver)
                .contentType("application/json").port(port).body(car)
                .when().post("/api/v1/drivers/cars")
                .then().log().ifValidationFails(LogDetail.BODY).extract().response();
        this.carId = response.jsonPath().getString("id");
    }

    @BeforeEach
    void setup(){
        setAuthenticationTokenDriver();
        setAuthenticationTokenPassenger();
        setCarId();
    }

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should return 201 status code when creating a ride")
    void shouldReturn201StatusCodeWhenCreatingARide(){
        final RideRequestModel ride = RideEntityBuilder.createRandomRide(carId);
        given().header("Authorization", "Bearer " + authenticationTokenDriver)
                .contentType("application/json").port(port).body(ride)
                .when().post("/api/v1/ride")
                .then().log().ifValidationFails(LogDetail.BODY).statusCode(201).body("rideId",notNullValue());
    }

    @Test
    @Tag("ApitTest")
    @Tag("IntegrationTest")
    @DisplayName("Should return 400 status code when creating a ride but no body")
    void shouldReturn400StatusCodeWhenCreatingARideButNoBody(){
        given().header("Authorization", "Bearer " + authenticationTokenDriver)
                .contentType("application/json").port(port)
                .when().post("/api/v1/ride")
                .then().log().ifValidationFails(LogDetail.BODY).statusCode(400);
    }

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should return 200 when get available rides and rides in body")
    void shouldReturn200WhenGetAvailableRidesAndRidesInBody(){
        final RideRequestModel ride = RideEntityBuilder.createRandomRide(carId);
        given().header("Authorization", "Bearer " + authenticationTokenDriver)
                .contentType("application/json").port(port).body(ride)
                .when().post("/api/v1/ride")
                .then().log().ifValidationFails(LogDetail.BODY);

        given().header("Authorization", "Bearer " + authenticationTokenPassenger)
                .when().get("/api/v1/ride")
                .then().log().ifValidationFails(LogDetail.BODY).statusCode(200)
                .body("$", hasSize(1))
                .body("[0]", notNullValue());
    }

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should return 200 when get rides by Id with the ride in body")
    void shouldReturn200WhenGetRidesByIdWithTheRideInBody(){
        final RideRequestModel ride = RideEntityBuilder.createRandomRide(carId);
        Response response = given().header("Authorization", "Bearer " + authenticationTokenDriver)
                .contentType("application/json").port(port).body(ride)
                .when().post("/api/v1/ride")
                .then().log().ifValidationFails(LogDetail.BODY).extract().response();
        String id = response.jsonPath().getString("rideId");

        given().header("Authorization", "Bearer " + authenticationTokenDriver)
                .when().get("/api/v1/ride/"+id)
                .then().log().ifValidationFails(LogDetail.BODY).statusCode(200).body("uuid", equalTo(id));
    }

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should return 204 status code when deleting ride")
    void shouldReturn204StatusCodeWhenDeletingRide(){
        final RideRequestModel ride = RideEntityBuilder.createRandomRide(carId);
        Response response = given().header("Authorization", "Bearer " + authenticationTokenDriver)
                .contentType("application/json").port(port).body(ride)
                .when().post("/api/v1/ride")
                .then().log().ifValidationFails(LogDetail.BODY).extract().response();
        String id = response.jsonPath().getString("rideId");

        given().header("Authorization", "Bearer " + authenticationTokenDriver)
                .when().delete("/api/v1/ride/"+id)
                .then().log().ifValidationFails(LogDetail.BODY).statusCode(204);
    }

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should return 200 when passenger abandon ride")
    void shouldReturn200WhenPassengerAbandonRide(){
        //making ride
        final RideRequestModel ride = RideEntityBuilder.createRandomRide(carId);
        Response response = given().header("Authorization", "Bearer " + authenticationTokenDriver)
                .contentType("application/json").port(port).body(ride)
                .when().post("/api/v1/ride")
                .then().log().ifValidationFails(LogDetail.BODY).extract().response();
        String rideId = response.jsonPath().getString("rideId");

        //making ride solicitation
        given().header("Authorization", "Bearer " + authenticationTokenPassenger)
                .when().post("/api/v1/ride-solicitations?rideId="+rideId)
                .then().log().ifValidationFails(LogDetail.BODY);

        //get driver ride solicitation
        Response solicitationResponse = given().header("Authorization", "Bearer " + authenticationTokenDriver)
                .when().get("/api/v1/ride-solicitations/driver/pending")
                .then().log().ifValidationFails(LogDetail.BODY).extract().response();

        String solicitationId = solicitationResponse.jsonPath().getString("[0].rideSolicitationId");

        //accepting
        given().header("Authorization", "Bearer " + authenticationTokenDriver)
                .when().post("/api/v1/ride-solicitations/"+solicitationId+"/accept")
                .then().log().ifValidationFails(LogDetail.BODY);

        //abandon ride
        given().header("Authorization", "Bearer " + authenticationTokenPassenger)
                .when().put("/api/v1/ride/"+rideId+"/passenger/abandon")
                .then().log().ifValidationFails(LogDetail.BODY).statusCode(200);
    }

}
