package br.ifsp.demo.integration.api.tests;

import br.ifsp.demo.integration.api.utils.BaseApiIntegrationTest;
import br.ifsp.demo.integration.api.utils.DriverEntityBuilder;
import br.ifsp.demo.integration.api.utils.PassengerEntityBuilder;
import br.ifsp.demo.integration.api.utils.RegisterUserRequest;
import br.ifsp.demo.security.auth.AuthRequest;
import io.restassured.filter.log.LogDetail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class AuthTests extends BaseApiIntegrationTest {

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should register passenger and return 201 with id as payload")
    void shouldRegisterUserAndReturn201WithIdAsPayload(){
        final RegisterUserRequest user = PassengerEntityBuilder.createRandomPassengerUser("password");
        given().contentType("application/json").port(port).body(user)
                .when().post("/api/v1/register")
                .then().log().ifValidationFails(LogDetail.BODY).statusCode(201).body("id", notNullValue());
    }

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should register driver and return 201 with id as payload")
    void shouldRegisterDriverAndReturn201WithIdAsPayload(){
        final RegisterUserRequest user = DriverEntityBuilder.createRandomDriverUser("password");
        given().contentType("application/json").port(port).body(user)
                .when().post("/api/v1/register")
                .then().log().ifValidationFails(LogDetail.BODY).statusCode(201).body("id", notNullValue());
    }

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should return code 409 if email is already used")
    void shouldReturnCode409IfEmailIsAlreadyUsed(){
        final RegisterUserRequest user1 = DriverEntityBuilder.createDriverByEmail("password", "email@email.com");
        final RegisterUserRequest user2 = DriverEntityBuilder.createDriverByEmail("password", "email@email.com");
        given().contentType("application/json").port(port).body(user1)
                .when().post("/api/v1/register")
                .then().log().ifValidationFails(LogDetail.BODY).statusCode(201).body("id", notNullValue());
        given().contentType("application/json").port(port).body(user2)
                .when().post("/api/v1/register")
                .then().log().ifValidationFails(LogDetail.BODY).statusCode(409);
    }

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should login a user and returns 200 and token payload")
    void shouldLoginAUserAndReturns200AndTokenPayload(){
        final RegisterUserRequest user = PassengerEntityBuilder.createPassengerByEmail("password", "email@email.com");
        given().contentType("application/json").port(port).body(user)
                .when().post("/api/v1/register");

        final AuthRequest authRequest = new AuthRequest("email@email.com","password");
        given().contentType("application/json").port(port).body(authRequest)
                .when().post("/api/v1/authenticate")
                .then().log().ifValidationFails(LogDetail.BODY).statusCode(200).body("token", notNullValue());
    }

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should not login user and returns 401")
    void shouldNotLoginUserAndReturns401(){
        final RegisterUserRequest user = PassengerEntityBuilder.createPassengerByEmail("password", "email@email.com");
        given().contentType("application/json").port(port).body(user)
                .when().post("/api/v1/register");

        final AuthRequest authRequest = new AuthRequest("email@email.com","");
        given().contentType("application/json").port(port).body(authRequest)
                .when().post("/api/v1/authenticate")
                .then().log().ifValidationFails(LogDetail.BODY).statusCode(401);
    }
    
    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should return 400 if bad request")
    void shouldReturn400IfBadRequest(){
        given().contentType("application/json").port(port).body("")
                .when().post("/api/v1/authenticate")
                .then().log().ifValidationFails(LogDetail.BODY).statusCode(400);
    }

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should return 400 if cpf is null")
    void shouldReturn400IfCpfIsNull(){
        final RegisterUserRequest user = DriverEntityBuilder.createWithNullCPF();
        given().contentType("application/json").port(port).body(user)
                .when().post("/api/v1/register")
                .then().log().ifValidationFails(LogDetail.BODY).statusCode(400);
    }

}
