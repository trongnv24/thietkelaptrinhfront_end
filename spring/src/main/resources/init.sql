drop table if exists roles;

create table roles (
    id bigserial primary key,
    name varchar(255)
);

insert into roles values (1, 'ROLE_ADMIN');
insert into roles values (2, 'ROLE_USER');