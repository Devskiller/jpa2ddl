create table public.User (
                      id int8 not null,
                      date timestamp,
                      primary key (id)
);
create sequence hibernate_sequence start 1 increment 1;