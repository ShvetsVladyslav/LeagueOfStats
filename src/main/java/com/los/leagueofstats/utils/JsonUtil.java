package com.los.leagueofstats.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.los.leagueofstats.utils.mappers.CustomJsonMapper;
import com.los.leagueofstats.utils.mappers.JsonException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public final class JsonUtil {

    // <editor-fold defaultstate="collapsed" desc="***Элементы сопровождения***">

    public static final ObjectMapper OBJECT_MAPPER = new CustomJsonMapper();
    public static final ObjectWriter OBJECT_WRITER = OBJECT_MAPPER.writerWithDefaultPrettyPrinter();

    // </editor-fold>

    private JsonUtil() {
    }

    /**
     * Преобразование объекта в PrettyJson.
     *
     * @param obj объект для преобразования.
     *
     * @return преобразованный в строку объект или JsonException.
     */
    public static String toPrettyJson(Object obj) {
        try {
            return OBJECT_WRITER.writeValueAsString(obj);
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }

    /**
     * Преобразование объекта в Json.
     *
     * @param obj объект для преобразования.
     *
     * @return преобразованный в строку объект или JsonException.
     */
    public static String toJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }

    /**
     * Преобразование объекта в массив байт.
     *
     * @param obj объект для преобразования.
     *
     * @return преобразованный в массив байт объект или JsonException.
     */
    public static byte[] writeValueAsBytes(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(obj);
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }

    /**
     * Получение объект из Json.
     *
     * @param json json-строка для преобразования в объект.
     * @param <T>  класс объекта для преобразования.
     *
     * @return преобразованный объект или JsonException.
     */
    public static <T> T fromJson(String json,
                                 TypeReference<T> typeReference) throws JsonException {
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }

    /**
     * Получение объект из Json.
     *
     * @param json json-строка для преобразования в объект.
     * @param <T>  класс объекта для преобразования.
     *
     * @return преобразованный объект или JsonException.
     */
    public static <T> T fromJson(String json,
                                 Class<T> clazz) throws JsonException {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }

    /**
     * Получение объект из Json.
     *
     * @param file файл, из которого будет производиться чтение.
     * @param <T>  класс объекта для преобразования.
     *
     * @return преобразованный объект или JsonException.
     */
    public static <T> T fromJson(File file,
                                 TypeReference<T> typeReference) throws JsonException {
        try {
            return OBJECT_MAPPER.readValue(file, typeReference);
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }

    /**
     * Получение объект из Json.
     *
     * @param is  InputStream с данными.
     * @param <T> класс объекта для преобразования.
     *
     * @return преобразованный объект или JsonException.
     */
    public static <T> T fromJson(InputStream is,
                                 TypeReference<T> typeReference) throws JsonException {
        try {
            return OBJECT_MAPPER.readValue(is, typeReference);
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }

    /**
     * Получение объект из массива байт.
     *
     * @param bytes массив байт с данными.
     * @param <T>   класс объекта для преобразования.
     *
     * @return преобразованный объект или JsonException.
     */
    public static <T> T readValue(byte[] bytes,
                                  TypeReference<T> typeReference) throws JsonException {
        try {
            return OBJECT_MAPPER.readValue(bytes, typeReference);
        } catch (Exception e) {
            throw new JsonException(e);
        }
    }
}
