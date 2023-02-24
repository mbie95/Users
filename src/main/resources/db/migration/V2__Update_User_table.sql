alter table users
drop column phone_number;
alter table users
add column phone_number int null unique;
