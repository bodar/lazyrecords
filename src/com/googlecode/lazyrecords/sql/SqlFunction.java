package com.googlecode.lazyrecords.sql;

import com.googlecode.totallylazy.functions.Function1;

@java.lang.annotation.Target(java.lang.annotation.ElementType.METHOD)
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface SqlFunction {
    java.lang.String value();

    class functions {
        public static Function1<SqlFunction, String> value() {
            return SqlFunction::value;
        }
    }
}
