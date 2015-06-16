package com.googlecode.lazyrecords;

import com.googlecode.totallylazy.Function;

import java.util.Map;

public interface Logger {

    Logger log(final Map<String, ?> parameters);

    public static class functions{
        private functions() {}

        public static Function<Logger, Logger> log(final Map<String, ?> parameters){
            return new Function<Logger, Logger>() {
                @Override
                public Logger call(Logger logger) throws Exception {
                    return logger.log(parameters);
                }
            };
        }
    }

}
