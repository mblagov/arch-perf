package com.mblagov.data.gen.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Person {

    private String id;
    private String firstName;
    private String lastName;
    private String middleName;
    private LocalDate dateOfBirth;
    private String address;
    private String comment;

    private LocalDateTime generatedTs;

    public Person(String firstName, String lastName, String middleName, LocalDate dateOfBirth, String address, String comment, LocalDateTime generatedTs) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.comment = comment;
        this.generatedTs = generatedTs;
    }

    public Person(String id, String firstName, String lastName, String middleName, LocalDate dateOfBirth, String address, String comment, LocalDateTime generatedTs) {
        this.id = id;
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

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getGeneratedTs() {
        return generatedTs;
    }

    public void setGeneratedTs(LocalDateTime generatedTs) {
        this.generatedTs = generatedTs;
    }
}
