-- Удаляем таблицы, если они существуют (обратный порядок, чтобы не было ошибок)
DROP TABLE IF EXISTS bookings, items, requests, users;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE requests (
    id SERIAL PRIMARY KEY,
    description TEXT NOT NULL,
    requestor_id INT NOT NULL,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (requestor_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE items (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    owner_id INT NOT NULL,
    request_id INT,
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (request_id) REFERENCES requests(id) ON DELETE SET NULL
);


CREATE TABLE bookings (
    id SERIAL PRIMARY KEY,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    item_id INT NOT NULL,
    booker_id INT NOT NULL,
    status VARCHAR(20) CHECK (status IN ('WAITING', 'APPROVED', 'REJECTED', 'CANCELED')),
    FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE,
    FOREIGN KEY (booker_id) REFERENCES users(id) ON DELETE CASCADE
);
