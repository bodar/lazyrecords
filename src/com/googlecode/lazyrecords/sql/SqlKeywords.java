package com.googlecode.lazyrecords.sql;

import com.googlecode.lazyrecords.Keyword;
import com.googlecode.lazyrecords.Keywords;

public class SqlKeywords {
    public static Keyword<Object> keyword(String value) {
        return keyword(value, Object.class);
    }

    public static <T> Keyword<T> keyword(String value, Class<T> aClass) {
        if(value.contains(".")){
            return Keywords.keyword(value, aClass).as(Keywords.keyword(extractName(value), aClass));
        }
        return Keywords.keyword(value, aClass);
    }

    private static String extractName(String value) {
        String[] parts = value.split("\\.");
        return parts[parts.length - 1];
    }

}
