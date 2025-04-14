package com.los.leagueofstats.utils.mappers;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.los.leagueofstats.utils.serializers.EnumDeserializers;

public class CustomJsonModule extends SimpleModule {

    public CustomJsonModule() {
        super("CustomJsonModule");
        this.setDeserializerModifier(new EnumDeserializers());
        //        this.addDeserializer(LocalDateTime.class, new LDTDeserializer());
        //        this.addDeserializer(LocalDate.class, new LDDeserializer());
        //        this.addDeserializer(LocalTime.class, new LTDeserializer());
        //        this.addDeserializer(YearMonth.class, new YMDeserializer());

        //        this.addSerializer(LocalDateTime.class, new LDTSerializer());
        //        this.addSerializer(LocalDate.class, new LDSerializer());
        //        this.addSerializer(LocalTime.class, new LTSerializer());
    }
}
