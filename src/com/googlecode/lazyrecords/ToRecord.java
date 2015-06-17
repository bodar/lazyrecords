package com.googlecode.lazyrecords;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Mapper;

public abstract class ToRecord<T> extends Mapper<T, Record> implements ClientComputation {
    public static <T> ToRecord<T> toRecord(final Function1<? super T, Record> callable) {
        return new ToRecord<T>() {
            @Override
            public Record call(T t) throws Exception {
                return callable.call(t);
            }
        };
    }
}
