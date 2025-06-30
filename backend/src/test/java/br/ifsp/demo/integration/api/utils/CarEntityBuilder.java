package br.ifsp.demo.integration.api.utils;

import br.ifsp.demo.domain.Car;
import br.ifsp.demo.domain.LicensePlate;
import br.ifsp.demo.models.request.CarRequestModel;
import net.datafaker.Faker;

import java.util.Locale;
import java.util.Random;

public class CarEntityBuilder {
    private static Faker faker = new Faker(new Locale("pt", "BR"));

    public static CarRequestModel createRandomCar(){
        Random rand = new Random();
        return new CarRequestModel(
                faker.vehicle().manufacturer(),
                faker.vehicle().model(),
                faker.vehicle().color(),
                rand.nextInt(6)+1,
                getPlate()
        );
    }

    private static String getPlate(){
        Random rand = new Random();
        if(rand.nextInt(2)==0){
            String oldPlateRegex = "[A-Z]{3}-\\d{4}";
            return faker.regexify(oldPlateRegex);

        }
        String mercosulRegex = "[A-Z]{3}[0-9][A-Z][0-9]{2}";
        return faker.regexify(mercosulRegex);
    }
}
