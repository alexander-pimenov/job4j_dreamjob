--//создаем таблицу вакансий
create TABLE IF NOT EXISTS post (
    id SERIAL primary key,
    name TEXT,
    description TEXT
);