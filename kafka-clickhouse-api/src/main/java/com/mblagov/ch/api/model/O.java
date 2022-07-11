package com.mblagov.ch.api.model;

import java.time.LocalDateTime;

public class O {
    private String _id;
    private String first_name;
    private String last_name;
    private String middle_name;
    private Long date_of_birth;
    private String address;
    private String comment;
    private Long generated_ts;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public Long getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(Long date_of_birth) {
        this.date_of_birth = date_of_birth;
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

    public Long getGenerated_ts() {
        return generated_ts;
    }

    public void setGenerated_ts(Long generated_ts) {
        this.generated_ts = generated_ts;
    }

    @Override
    public String toString() {
        return "O{" +
                "_id='" + _id + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", middle_name='" + middle_name + '\'' +
                ", date_of_birth=" + date_of_birth +
                ", address='" + address + '\'' +
                ", comment='" + comment + '\'' +
                ", generated_ts=" + generated_ts +
                '}';
    }
}
