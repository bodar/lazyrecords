package com.googlecode.lazyrecords.sql.expressions;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;

public enum AnsiJoinType implements JoinType {
    inner,
    left;

    @Override
    public String text() {
        return name().toLowerCase();
    }

    @Override
    public Sequence<Object> parameters() {
        return Sequences.empty();
    }
}
