package br.ifsp.demo.integration.api.utils;

import br.ifsp.demo.models.request.RideRequestModel;
import net.datafaker.Faker;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RideEntityBuilder {

    private static Faker faker = new Faker();

    public static RideRequestModel createRandomRide(String carId){
        return new RideRequestModel(
                getAddress(),
                getAddress(),
                faker.timeAndDate().future(1, TimeUnit.DAYS).atZone(ZoneId.systemDefault()).toLocalDateTime(),
                UUID.fromString(carId)
        );
    }

    private static String getAddress(){
        return String.format(
                "%s, %s, %s, %s",
                faker.address().streetName(),
                faker.address().buildingNumber(),
                faker.address().city(),
                faker.address().state()
        );
    }
}
