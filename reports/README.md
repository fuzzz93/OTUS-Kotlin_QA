# Allure-отчёты

Отчёт по автотестам домашнего задания (REST API списка задач, MockWebServer).

## Содержимое

- `allure-single/index.html` — самодостаточный отчёт в одном файле.
  Открывается двойным кликом прямо из файловой системы (`file://`), интернет не нужен.

## Как пересобрать

```bash
./gradlew clean test          # прогнать тесты → build/allure-results
allure generate build/allure-results --clean --single-file -o reports/allure-single
```

> Плагин `io.qameta.allure` под Gradle 9 генерирует только `allure-results`
> (adapter), а задача `allureReport` несовместима с Gradle 9 — поэтому HTML
> собирается внешним Allure CLI (commandline 2.44.0).
