package com.googlecode.lazyrecords.simpledb.mappings;

public interface Mapping<T> {
    T toValue(String value) throws Exception;

    String toString(T value) throws Exception;
}
