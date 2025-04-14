package com.los.leagueofstats.utils.mappers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class CustomJsonMapper extends ObjectMapper {

    public CustomJsonMapper() {
//        setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
//        setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
//        setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
//        setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
        registerModule(new CustomJsonModule());
        registerModule(new JavaTimeModule());
        registerModule(new GuavaModule());
        /**
         * Если ставить fasle - jackson может объект создать, но со всеми полями null, не выбросив
         * ни одной шибки - тогда нужно будет результат десериализации проверять на != null и на наличие значения в
         * каком-то обязательно поле как != null. Либо оставлять true, но в Pojo прописывать абсолютно все поля, которые
         * могут вернуться из АПИ.
         */
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
