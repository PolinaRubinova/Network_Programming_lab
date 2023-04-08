# Network_Programming_lab
Блокчейн, написанный на языке Kotlin с использованием фреймворка Ktor.

---

## Тестирование
Ветка main: [![Tests](https://github.com/PolinaRubinova/Network_Programming_lab/actions/workflows/gradle-tests.yml/badge.svg?branch=main)](https://github.com/PolinaRubinova/Network_Programming_lab/actions/workflows/gradle-tests.yml)

Ветка develop: [![Tests](https://github.com/PolinaRubinova/Network_Programming_lab/actions/workflows/gradle-tests.yml/badge.svg?branch=dev)](https://github.com/PolinaRubinova/Network_Programming_lab/actions/workflows/gradle-tests.yml)

Для тестирования приложения были реализованы модульные тесты (тестируют логику отдельных элементов блокчейна) и интеграционные тесты (тестируют взаимодействие элементов блокчейна).

---

## Запуск проекта
Для запуска собрать артефакт: `gradlew.bat jar`

Созданный артефакт находится в `/build/libs`.

Запускать через командную строку:
``` console
java -jar com.example.blockchain-without-strain-0.0.1.jar 8080 8081 8082 1
```
Аргументы:
* [0] - порт текущей ноды
* [1] - порт первой ноды
* [2] - порт второй ноды
* [3] - флаг: является ли текущая нода главной ("1" - главная, "0" - не является главной)

Аналогично запустить другие ноды блокчейна.

---

## Docker
1. Клонировать репозиторий
``` console
$ git clone https://github.com/PolinaRubinova/Network_Programming_lab.git`
```

2. Собрать Docker образ
```console
$ docker build -t Network_Programming_lab .
```

3. Запустить Docker Compose
```console
$ docker-compose up
```