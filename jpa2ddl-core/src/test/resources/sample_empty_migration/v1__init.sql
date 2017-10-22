create table User (
  id bigint not null,
  date datetime(6),
  email varchar(255),
  primary key (id)
) engine=InnoDB;