package com.los.leagueofstats.config;

import com.google.common.collect.ImmutableMap;
import com.los.leagueofstats.utils.SpringUtils;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;


import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

@Log4j2
@Data
@Component
public class AppProperties {

    /**
     * карта настроек: ключ property, значение property
     */
    private final Map<String, String> propMap;


    @Autowired
    public AppProperties(ConfigurableEnvironment env) {
        log.trace("*** env = {}", env);

        // получаем настройки в карту
        Map<String, String> props = SpringUtils.getAllProperties(env);
        propMap = ImmutableMap.copyOf(props);
    }


    /**
     * Получает значение свойства.
     * если не найдено - выдаст null
     *
     * @param key ключ свойства
     *
     * @return значение свойства
     */
    public String getProperty(String key) {
        checkArgument(key != null, "Property key not specified");
        return propMap.get(key);
    }

    /**
     * Получает значение свойства.
     * если не найдено - выдаст исключение
     *
     * @param key ключ свойства
     *
     * @return значение свойства
     */
    public String getRequiredProperty(String key) {
        checkArgument(key != null, "Property key not specified");
        String value = getProperty(key);

        checkState(value != null, "Can not find property %s", key);
        return value;
    }

    /**
     * Получает значение свойства.<br>
     * Если не найдено - выдаст исключение.
     *
     * @param key       ключ свойства.
     * @param converter converter to convert string property into desired response type.
     *
     * @return значение свойства.
     *
     * @since 0.11.9.0
     */
    public <T> T getRequiredProperty(String key,
                                     AppPropertiesConverter<String, T> converter) {
        return converter.convert(getRequiredProperty(key));
    }




    /**
     * @since 0.7.0
     */
    public void checkPropertyExists(String key) {
        checkArgument(key != null, "Property key is not specified!");
        checkArgument(getProperty(key) != null, "Property value for key '%s' is not found!", key);
    }

}
