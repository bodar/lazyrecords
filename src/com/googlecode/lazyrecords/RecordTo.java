package com.googlecode.lazyrecords;

import com.googlecode.totallylazy.functions.Function1;

public abstract class RecordTo<R> implements Function1<Record, R>, ClientComputation {
    public static <R> RecordTo<R> recordTo(final Function1<Record, ? extends R> callable) {
        return new RecordTo<R>() {
            @Override
            public R call(Record record) throws Exception {
                return callable.call(record);
            }
        };
    }
}