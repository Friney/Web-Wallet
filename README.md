# Web wallet

* [Описание проекта](#описание-проекта)
* [Документация API](#документация-api)
* [Сборка](#сборка)

## Описание проекта

Это серверная часть электронного кошелька.

* JDK 17
* Spring
* PostgreSQL
* Gradle
* Docker

## Документация API

Документация к api написанна в [Api.yaml](./Api.yaml)

## Сборка

* Для запуска приложения требуется запустить базу данных<br>
  Находясь в корне проекта, выполните команду `docker-compose up postgres`
  <br></br>
* После запуска приложения, будет автоматически выполнен накат миграций Flyway.
