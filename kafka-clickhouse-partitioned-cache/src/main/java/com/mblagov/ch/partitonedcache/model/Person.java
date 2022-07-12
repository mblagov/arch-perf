package com.mblagov.ch.partitonedcache.model;

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

    private final LocalDateTime ts_wrote_to_mongo;
    private final LocalDateTime ts_read_from_kafka;
    private final LocalDateTime ts_wrote_to_ch;
    private final boolean is_deleted;
    private final LocalDateTime generatedTs;

    public Person(String id,
                  String firstName,
                  String lastName,
                  String middleName,
                  LocalDate dateOfBirth,
                  String address,
                  String comment,
                  LocalDateTime ts_wrote_to_mongo,
                  LocalDateTime ts_read_from_kafka,
                  LocalDateTime ts_wrote_to_ch,
                  boolean is_deleted,
                  LocalDateTime generatedTs) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.comment = comment;
        this.ts_wrote_to_mongo = ts_wrote_to_mongo;
        this.ts_read_from_kafka = ts_read_from_kafka;
        this.ts_wrote_to_ch = ts_wrote_to_ch;
        this.is_deleted = is_deleted;
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

    public LocalDateTime getTs_wrote_to_mongo() {
        return ts_wrote_to_mongo;
    }

    public LocalDateTime getTs_read_from_kafka() {
        return ts_read_from_kafka;
    }

    public LocalDateTime getTs_wrote_to_ch() {
        return ts_wrote_to_ch;
    }

    public boolean getIs_deleted() {
        return is_deleted;
    }
}
