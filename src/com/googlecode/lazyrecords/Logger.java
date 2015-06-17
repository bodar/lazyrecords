package com.googlecode.lazyrecords;

import com.googlecode.totallylazy.Function1;

import java.util.Map;

public interface Logger {

    Logger log(final Map<String, ?> parameters);

    public static class functions{
        private functions() {}

        public static Function1<Logger, Logger> log(final Map<String, ?> parameters){
            return logger -> logger.log(parameters);
        }
    }

}
