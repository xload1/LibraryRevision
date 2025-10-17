create table books (
                       id bigserial primary key,
                       title text not null,
                       isbn text not null unique,
                       stock int not null default 0,
                       published_at timestamp
);
