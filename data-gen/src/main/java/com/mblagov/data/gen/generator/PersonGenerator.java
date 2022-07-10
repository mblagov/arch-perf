package com.mblagov.data.gen.generator;

import com.mblagov.data.gen.model.Person;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PersonGenerator {

    public List<Person> generateRandomPersons(int numberOfRecords) {
        List<Person> people = new ArrayList<>();

        for (int i = 0; i < numberOfRecords; i++) {
            people.add(getRandomPerson());
        }

        return people;
    }

    public Person getRandomPersonWithId(String id) {
        Person randomPerson = getRandomPerson();
        randomPerson.setId(id);
        return randomPerson;
    }

    public Person getRandomPerson() {
        String firstName = generateRandomString(20);
        String lastName = generateRandomString(20);
        String middleName = generateRandomString(20);
        LocalDate dateOfBirth = random2thCenturyDate();
        String address = generateRandomString(250);
        String comment = generateRandomString(1000);

        return new Person(firstName, lastName, middleName, dateOfBirth, address, comment);
    }

    private LocalDate random2thCenturyDate() {
        Random random = new Random();
        int year = random.nextInt(99) + 1900;
        int month = random.nextInt(11) + 1;
        int day = random.nextInt(27) + 1;

        return LocalDate.of(year, month, day);
    }

    private String generateRandomString(int targetStringLength) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1).filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(targetStringLength).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

        return generatedString;
    }

}
