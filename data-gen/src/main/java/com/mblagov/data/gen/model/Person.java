package com.mblagov.data.gen.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Person {

    private String id;
    private final String firstName;
    private final String lastName;
    private final String middleName;
    private final LocalDate dateOfBirth;
    private final String address;
    private final String comment;

    private final LocalDateTime generatedTs;

    public Person(String firstName, String lastName, String middleName, LocalDate dateOfBirth, String address, String comment, LocalDateTime generatedTs) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.comment = comment;
        this.generatedTs = generatedTs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }


    public String getLastName() {
        return lastName;
    }


    public String getMiddleName() {
        return middleName;
    }


    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }


    public String getAddress() {
        return address;
    }


    public String getComment() {
        return comment;
    }


    public LocalDateTime getGeneratedTs() {
        return generatedTs;
    }

}
