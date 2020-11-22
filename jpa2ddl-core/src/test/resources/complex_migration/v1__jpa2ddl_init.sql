drop table if exists Book CASCADE ;

drop table if exists Book_Chapter CASCADE ;

drop table if exists Chapter CASCADE ;

create table Book (
                      id bigint not null,
                      primary key (id)
);

create table Book_Chapter (
                              Book_id bigint not null,
                              chapters_id bigint not null,
                              primary key (Book_id, chapters_id)
);

create table Chapter (
                         id bigint not null,
                         primary key (id)
);
create index fk_book_chapter on Book_Chapter (Book_id);

alter table Book_Chapter
    add constraint UK_tokwoktdrnoke2coi76lqff1s unique (chapters_id);

alter table Book_Chapter
    add constraint FKcsndnril8gfgxo6f7osu292dr
        foreign key (chapters_id)
            references Chapter;

alter table Book_Chapter
    add constraint FKrylp6x2fsgdveg71fhss6riby
        foreign key (Book_id)
            references Book
            on delete cascade;
