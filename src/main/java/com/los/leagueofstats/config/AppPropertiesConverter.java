package com.los.leagueofstats.config;

@FunctionalInterface
public interface AppPropertiesConverter<F, T> {
    T convert(F from);
}
