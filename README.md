# Riot Stats Bot

**Pet‑проект**: Telegram‑бот на Java 17 / Spring Boot 2.7, показывающий статистику из Riot Games API (LoL, TFT, …). Цель — углубить навыки разработки у автора и дать практику тестировщику.

---

## 📑 Содержание
1. [Стек технологий](#-стек-технологий)
2. [Функциональность](#-функциональность)
3. [Конфигурация](#-конфигурация)
4. [Как добавить новую команду](#-как-добавить-новую-команду)
5. [Roadmap](#-roadmap)
6. [Лицензия](#-лицензия)

---

## 🛠️ Стек технологий
| Слой | Технологии |
|------|------------|
| Язык | **Java 17** |
| Framework | **Spring Boot 2.7.18** |
| Telegram SDK | `telegrambots-spring-boot-starter` 6.9.7.1 |
| Сборка | Maven 3.9 |
| CI / VCS | GitHub Actions, Git |

---

## ✨ Функциональность
- `/start` — приветствие и список доступных команд.
- Обработка неизвестных команд: вежливое сообщение «Неизвестная команда. Напишите /start для справки».
- Роутер на основе `enum TelegramBotCommands` + отдельные Spring‑компоненты‑хендлеры.

> В следующих релизах появятся: поиск суммонера, матч‑история, live‑game, TFT‑статы и т.д.

---

## 🔧 Конфигурация

| Переменная | Описание |
|------------|----------|
| `TELEGRAM_BOT_TOKEN` | токен, полученный у BotFather |
| `TELEGRAM_BOT_USERNAME` | `@имя_бота` без `@` |
| `RIOT_API_KEY` | ключ Riot Games API |

### .env
Храните секреты локально в файле `.env`:
```
TELEGRAM_BOT_TOKEN=123456:ABC‑DEF…
TELEGRAM_BOT_USERNAME=my_riot_bot
RIOT_API_KEY=RGAPI‑xxxxxxxx
```
IDEA + плагин **EnvFile** автоматически подставит значения при запуске.

### application.yml
```yaml
spring:
  config:
    import: optional:file:.env  # подхватывается EnvFile / direnv
telegram:
  bot:
    username: ${TELEGRAM_BOT_USERNAME}
    token: ${TELEGRAM_BOT_TOKEN}
riot:
  api:
    key: ${RIOT_API_KEY}
    region: euw1
```

---

## ➕ Как добавить новую команду
1. **Enum**: добавить элемент в `TelegramBotCommands`, например `MATCHES("/matches")`.
2. **Хендлер**: создать класс `MatchesCommandHandler` в пакете `bot.command.impl`, реализовать `BotCommandHandler`, вернуть нужный enum в `command()`.
3. **Логика**: внутри `handle()` вызвать сервис, сформировать `SendMessage`.
4. Готово — бот автоматически зарегистрирует новый хендлер.

---

## 🛣️ Roadmap
- [ ] Интеграция с Riot API

---

## 📄 Лицензия
MIT — см. `LICENSE`.

