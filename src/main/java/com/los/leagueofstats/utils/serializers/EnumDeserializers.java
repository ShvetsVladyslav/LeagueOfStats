package com.los.leagueofstats.utils.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import org.apache.commons.lang3.EnumUtils;

import java.io.IOException;

public class EnumDeserializers extends BeanDeserializerModifier {

    /*Took here: http://stackoverflow.com/questions/24157817/jackson-databind-enum-case-insensitive#answer-24173645*/

    @Override
    public JsonDeserializer<Enum> modifyEnumDeserializer(DeserializationConfig config,
                                                         JavaType type,
                                                         BeanDescription beanDesc,
                                                         JsonDeserializer<?> deserializer) {
        return new JsonDeserializer<Enum>() {
            @Override
            public Enum deserialize(JsonParser p,
                                    DeserializationContext ctxt) throws IOException {
                Class<? extends Enum> rawClass = (Class<Enum<?>>) type.getRawClass();

                String valueAsString = p.getValueAsString();
                if (valueAsString == null) {
                    return null;
                }

                String stringValue = valueAsString.toUpperCase();

                Enum value = EnumUtils.getEnum(rawClass, stringValue);
                if (value == null) {
                    value = EnumUtils.getEnumIgnoreCase(rawClass, stringValue);
                }

                if (value == null
                        && !ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {  // если не установлено воспринимать неизвестные значения enum как null - выдаем исключение
                    throw new IllegalArgumentException("Unexpected enum value " + stringValue);
                }

                return value;
            }
        };
    }
}
