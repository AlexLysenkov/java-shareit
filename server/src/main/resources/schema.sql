CREATE TABLE IF NOT EXISTS users
(
   id    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
   name  VARCHAR(250) NOT NULL,
   email VARCHAR(500) NOT NULL,
   CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items
(
   id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
   name         VARCHAR(250) NOT NULL,
   description  VARCHAR(500) NOT NULL,
   is_available BOOLEAN      NOT NULL,
   owner_id     BIGINT       NOT NULL,
   request_id   BIGINT,
   FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings
(
   id         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
   start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
   end_date   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
   item_id    BIGINT                      NOT NULL,
   booker_id  BIGINT                      NOT NUll,
   status     VARCHAR                     NOT NULL,
   FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
   FOREIGN KEY (booker_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments
(
   id          BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
   text        VARCHAR(250)                     NOT NULl,
   item_id     BIGINT                           NOT NUll,
   author_id   BIGINT                           NOT NULl,
   create_date TIMESTAMP WITHOUT TIME ZONE      NOT NULL,
   FOREIGN KEY (item_id) REFERENCES items (id) ON DELETE CASCADE,
   FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description  VARCHAR(500)                     NOT NULL,
    requester_id BIGINT                           NOT NULL,
    create_date  TIMESTAMP WITHOUT TIME ZONE      NOT NULL,
    FOREIGN KEY (requester_id) REFERENCES users (id) ON DELETE CASCADE
);
