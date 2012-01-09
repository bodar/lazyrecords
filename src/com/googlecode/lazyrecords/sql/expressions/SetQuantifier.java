package com.googlecode.lazyrecords.sql.expressions;

public enum SetQuantifier {
    DISTINCT,
    ALL;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
