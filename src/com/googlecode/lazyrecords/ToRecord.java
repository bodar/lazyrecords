package com.googlecode.lazyrecords;

import com.googlecode.totallylazy.functions.Function1;

public abstract class ToRecord<T> implements Function1<T, Record>, ClientComputation {
    public static <T> ToRecord<T> toRecord(final Function1<? super T, Record> callable) {
        return new ToRecord<T>() {
            @Override
            public Record call(T t) throws Exception {
                return callable.call(t);
            }
        };
    }
}
