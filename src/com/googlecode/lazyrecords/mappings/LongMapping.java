package com.googlecode.lazyrecords.mappings;

import com.googlecode.totallylazy.numbers.Numbers;

import static com.googlecode.totallylazy.numbers.Numbers.parseLexicalString;

public class LongMapping implements StringMapping<Long> {
    public Long toValue(String value) {
        return Long.parseLong(value);
    }

    public String toString(Long value) {
        return Long.toString(value);
    }
}
