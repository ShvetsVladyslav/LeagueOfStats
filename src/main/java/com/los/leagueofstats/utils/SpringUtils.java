package com.los.leagueofstats.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

@Slf4j
public class SpringUtils {

    /**
     * Получает почти все значения properties для спринга.
     * Не удастся получить значения из PropertySource, которые не итерируются,
     *   например JndiPropertySource, StubPropertySource, PropertySource из EndpointWebMvcAutoConfiguration ...
     * @param env ConfigurableEnvironment
     * @return карта: ключ свойства, значение свойства
     */
    public static Map<String, String> getAllProperties(ConfigurableEnvironment env) {
        Set<String> keys = new HashSet<>();     // set для складывания ключей настроек
        // получаем имена ключей настроек
        for (PropertySource<?> ps : env.getPropertySources()) {
            keys.addAll(getAllPropertiesNames(ps));
        }

        // по ключам получаем значения настройки
        Map<String, String> propMap = keys.stream()
                .collect(Collectors.toMap(key -> key, key -> env.getProperty(key)));
        return propMap;
    }

    /**
     * Получает имена ключей настроек
     * @param ps PropertySource
     * @return имена ключей настроек
     */
    private static Set<String> getAllPropertiesNames(PropertySource ps) {
        Set<String> keys = new HashSet<>();     // set для складывания ключей настроек

        // если из PropertySource можно получить имена настроек - получаем
        if (ps instanceof EnumerablePropertySource) {
            String[] propertyNames = ((EnumerablePropertySource) ps).getPropertyNames();
            keys.addAll(Arrays.asList(propertyNames));
            return keys;
        }

        // если PropertySource составной - получаем имена настроек для вложенных PropertySource
        if (ps instanceof CompositePropertySource) {
            CompositePropertySource cps = (CompositePropertySource) ps;
            for (PropertySource nestedPs : cps.getPropertySources()) {
                keys.addAll(getAllPropertiesNames(nestedPs));
            }
            return keys;
        }

        // если из PropertySource не удастся получить имена настроек - логируем
        // например JndiPropertySource, StubPropertySource, PropertySource из EndpointWebMvcAutoConfiguration ...
        log.debug("*** PropertySource is instanceof {} and cannot be iterated. "
                + "Cannot get property names from this PropertySource", ps.getClass().getName());
        return keys;
    }

    /**
     * Получает HttpServletRequest
     * @return HttpServletRequest или null, если информация о запросе отстутствует
     *   например текущий поток выполняется не в запросе spring
     */
    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        if (attrs == null) {
            return null;
        }
        return Optional.ofNullable(attrs)
                .map(ServletRequestAttributes::getRequest)
                .orElse(null);
    }

    /**
     * Set Jackson ObjectMapper to converters of specified type
     *
     * @param <T> type
     * @param converters    converters
     * @param objectMapper  ObjectMapper
     * @param converterType type
     */
    public static <T extends AbstractJackson2HttpMessageConverter> void setJacksonObjectMapper(
            List<HttpMessageConverter<?>> converters,
            ObjectMapper objectMapper,
            Class<T> converterType) {
        checkArgument(converters != null, "Converters are not specified");
        checkArgument(objectMapper != null, "Object mapper is not specified");
        checkArgument(objectMapper != null, "Converter type is not specified");
        converters.stream()
                .filter(c -> converterType.isInstance(c))
                .forEach(c -> ((AbstractJackson2HttpMessageConverter) c).setObjectMapper(objectMapper));
    }

}
