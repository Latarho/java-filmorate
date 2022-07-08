CREATE TABLE IF NOT EXISTS users (
     id bigint not null primary key,
     email varchar(64) unique not null,
     login varchar(64) unique not null,
     name varchar(64),
     birthday date not null
);

CREATE TABLE IF NOT EXISTS mpa_rating (
    id int not null primary key,
    name varchar(30) not null
);

CREATE TABLE IF NOT EXISTS genres (
    id int not null primary key,
    name varchar(50) not null
);

CREATE TABLE IF NOT EXISTS user_friendship (
    first_user_id bigint not null references users(id),
    second_user_id bigint not null references users(id),
    status boolean,
    unique (first_user_id, second_user_id)
);

CREATE TABLE IF NOT EXISTS films (
     id bigint not null primary key auto_increment,
     name varchar(100) unique not null,
     description varchar(1000) not null,
     release_date date not null,
     duration bigint not null,
     rate bigint,
     mpa_rating_id bigint not null references mpa_rating(id)
);

CREATE TABLE IF NOT EXISTS film_likes (
    film_id bigint not null references films(id),
    user_id bigint not null references users(id)
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id int not null references films(id),
    genre_id bigint not null references genres(id)
);