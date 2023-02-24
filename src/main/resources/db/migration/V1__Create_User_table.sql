drop table if exists users;
create table users (
    id int primary key auto_increment,
    first_name varchar(40) not null,
    last_name varchar(40) not null,
    birth_date date not null,
    phone_number int
);
