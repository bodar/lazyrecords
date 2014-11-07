package com.googlecode.lazyrecords;

public interface Transaction {
    void commit();
    void rollback();
}
