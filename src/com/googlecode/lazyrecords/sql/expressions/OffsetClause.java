package com.googlecode.lazyrecords.sql.expressions;

import com.googlecode.totallylazy.Function;

public interface OffsetClause extends Expression {
    int number();

    class functions {
        public static Function<OffsetClause, Integer> number() {
            return new Function<OffsetClause, Integer>() {
                @Override
                public Integer call(OffsetClause fetchClause) throws Exception {
                    return fetchClause.number();
                }
            };
        }

        public static Function<Integer, OffsetClause> offsetClause() {
            return new Function<Integer, OffsetClause>() {
                @Override
                public OffsetClause call(Integer integer) throws Exception {
                    return new AnsiOffsetClause(integer);
                }
            };
        }
    }
}
