# Примеры кода для ШБР

Примеры кода из первой части лекции для Школы разработки бэкенда

## Темы

* Подключение к БД
* Протокол простых запросов
* Протокол расширенных запросов
* Курсоры
* Транзакции
* Работа с данными

----

## Usage

Перед запуском тестов надо развернуть PostgreSQL. 
Можно сделать это в Docker с помощью команды:

```sh
docker run -e POSTGRES_PASSWORD=password -d -p 5432:5432 postgres
```
[Ссылка](https://hub.docker.com/_/postgres) на официальный образ PostgreSQL.

----

## Нагрузочное тестирование

Тестровать нагрузочно можно с помощью утилиты [wrk2](https://github.com/giltene/wrk2).
