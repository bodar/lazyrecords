package com.googlecode.lazyrecords.sql;

import com.googlecode.totallylazy.Function;

@java.lang.annotation.Target(java.lang.annotation.ElementType.METHOD)
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface SqlFunction {
    java.lang.String value();

    class functions {
        public static Function<SqlFunction, String> value() {
            return new Function<SqlFunction, String>() {
                @Override
                public String call(SqlFunction annotation) throws Exception {
                    return annotation.value();
                }
            };
        }
    }
}
