--//создаем таблицы связанные с кандидатами
CREATE TABLE IF NOT EXISTS candidate (
        id SERIAL PRIMARY KEY,
        name TEXT,
        photo_id INT REFERENCES photo(id)
);

CREATE TABLE IF NOT EXISTS photo (
        id SERIAL PRIMARY KEY,
        name VARCHAR(255),
        path TEXT
);