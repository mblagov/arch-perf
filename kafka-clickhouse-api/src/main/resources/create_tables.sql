create database if not exists  mblagov;

drop table if exists mblagov.person_data;

create table if not exists  mblagov.person_data
(
    id                 String,
    first_name         String,
    last_name          String,
    middle_name        String,
    date_of_birth      Date,
    address            String,
    comment            String,
    ts_generated       timestamp,
    ts_wrote_to_mongo  timestamp,
    ts_read_from_kafka timestamp,
    ts_wrote_to_ch     timestamp
) ENGINE = MergeTree()
      ORDER BY id;