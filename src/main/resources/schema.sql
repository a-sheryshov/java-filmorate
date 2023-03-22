
CREATE TABLE IF NOT EXISTS ratings (
    rating_id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(10)   NOT NULL,
    CONSTRAINT uc_rating_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS genres (
    genre_id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(30)   NOT NULL,
    CONSTRAINT uc_genre_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS films (
    film_id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(50)   NOT NULL,
    description varchar(200)   NOT NULL,
    release_date date   NOT NULL,
    duration integer   NOT NULL,
    rating_id bigint   NOT NULL,
	CONSTRAINT fk_film_rating_id FOREIGN KEY(rating_id) REFERENCES ratings (rating_id)
);

CREATE TABLE IF NOT EXISTS users (
    user_id bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email varchar(50)   NOT NULL,
    login varchar(50)   NOT NULL,
    name varchar(50)   NOT NULL,
    birthday date   NOT NULL,
    CONSTRAINT uc_user_email UNIQUE (email),
    CONSTRAINT uc_user_login UNIQUE (login)
);

CREATE TABLE IF NOT EXISTS films_likes (
    film_id bigint   NOT NULL,
    user_id bigint   NOT NULL,
    CONSTRAINT pk_films_likes PRIMARY KEY (film_id,user_id),
	CONSTRAINT fk_films_likes_film_id FOREIGN KEY(film_id) REFERENCES films (film_id),
	CONSTRAINT fk_films_likes_user_id FOREIGN KEY(user_id) REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS friendship (
    user_id1  bigint    NOT NULL,
    user_id2  bigint    NOT NULL,
    confirmed boolean   NOT NULL,
    CONSTRAINT pk_friendship PRIMARY KEY (user_id1, user_id2),
	CONSTRAINT fk_friendship_user_id1 FOREIGN KEY(user_id1) REFERENCES users (user_id),
	CONSTRAINT fk_friendship_user_id2 FOREIGN KEY(user_id2) REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS films_genres (
    film_id bigint   NOT NULL,
    genre_id bigint   NOT NULL,
    CONSTRAINT pk_films_genres PRIMARY KEY (film_id,genre_id),
	CONSTRAINT fk_films_genres_film_id FOREIGN KEY(film_id) REFERENCES films (film_id),
	CONSTRAINT fk_films_genres_genre_id FOREIGN KEY(genre_id) REFERENCES genres (genre_id)
);
