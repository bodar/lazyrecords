package com.googlecode.lazyrecords.sql.expressions;

import com.googlecode.totallylazy.Function1;

public interface OffsetClause extends Expression {
    int number();

    class functions {
        public static Function1<OffsetClause, Integer> number() {
            return OffsetClause::number;
        }

        public static Function1<Integer, OffsetClause> offsetClause() {
            return AnsiOffsetClause::new;
        }
    }
}
