-- Удаляем таблицы, если они существуют (обратный порядок, чтобы не было ошибок)
DROP TABLE IF EXISTS bookings, comments, items, requests, users;

-- Сначала создаем таблицу пользователей
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL
);

-- Создаем таблицу запросов, ссылающуюся на users
CREATE TABLE requests (
    id SERIAL PRIMARY KEY,
    description TEXT NOT NULL,
    requestor_id INT NOT NULL,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (requestor_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Создаем таблицу предметов, ссылающуюся на users и requests
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

-- Создаем таблицу бронирований, ссылающуюся на items и users
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

-- Создаем таблицу комментариев, ссылающуюся на items и users
CREATE TABLE comments (
    id SERIAL PRIMARY KEY,
    text TEXT NOT NULL,
    item_id INT NOT NULL,
    author_id INT NOT NULL,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);
