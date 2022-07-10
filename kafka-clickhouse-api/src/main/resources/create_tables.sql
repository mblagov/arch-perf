create database mblagov;

create table mblagov.person_data
(
    id            String,
    first_name    String,
    last_name     String,
    middle_name   String,
    date_of_birth Date,
    address       String,
    comment       String
) ENGINE = MergeTree()
      ORDER BY id;