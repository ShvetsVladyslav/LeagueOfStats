package com.los.leagueofstats.utils;

/**
 * Утилита для генерации типовых сообщений Telegram бота.
 */
public final class TelegramMessageUtils {

    private TelegramMessageUtils() {
        // Закрываем конструктор, чтобы не инстанцировали
    }

    /** Сообщение, если призыватель не найден */
    public static String summonerNotFound(String riotId) {
        return String.format(
                """
                😕 *Призыватель не найден*
                
                Я не смог найти игрока *%s*.
                
                • Проверьте, что ник и тег написаны без ошибок (регистр важен)  
                • Убедитесь, что выбран правильный регион  
                • Возможно, игрок недавно сменил Riot ID
                
                Попробуйте ещё раз чуть позже.
                """,
                riotId);
    }

    /** Сообщение, если формат Riot ID неправильный */
    public static String invalidRiotIdFormat(String riotId) {
        return String.format(
                """
                ⚠️ *Неверный формат никнейма*
                
                Вы ввели: *%s*
                
                • В нике разрешены буквы (латиница / кириллица) и пробелы
                • После `#` — 2–5 латинских букв, без цифр  
                
                Пример: `Faker#KR` или `Министр Бота#baddy`
                """,
                riotId);
    }

    /** Сообщение, если не удалось найти матчи */
    public static String noMatchData() {
        return """
                📭 Матчи не найдены.
                Возможно, аккаунт новый или произошла ошибка при получении данных.
                """;
    }

    /** Сообщение об ошибке Riot API */
    public static String riotApiError() {
        return """
                ⚠️ Riot API временно недоступен.
                Попробуй повторить запрос чуть позже.
                """;
    }

    /** Сообщение при превышении лимита запросов */
    public static String rateLimitExceeded() {
        return """
                ⏳ Riot просит подождать.
                Попробуй чуть позже
                """;
    }

    /** Сообщение при неизвестной ошибке Riot */
    public static String unknownError() {
        return """
                💥 Riot сейчас лежит.
                Вернёмся, когда встанет
                """;
    }

    /** Сообщение, если команда требует аргументы */
    public static String commandRequiresArguments(String command) {
        return "ℹ️ Команда *" + command + "* требует указания Riot ID.\n" +
                "Пример: `" + command + " Министр Бота#baddy`";
    }
}