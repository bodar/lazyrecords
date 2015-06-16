package com.googlecode.lazyrecords.sql.expressions;

import com.googlecode.totallylazy.Function;

public interface FetchClause extends Expression {
    int number();

    class functions {
        public static Function<FetchClause, Integer> number() {
            return new Function<FetchClause, Integer>() {
                @Override
                public Integer call(FetchClause fetchClause) throws Exception {
                    return fetchClause.number();
                }
            };
        }

        public static Function<Integer, FetchClause> fetchClause() {
            return new Function<Integer, FetchClause>() {
                @Override
                public FetchClause call(Integer integer) throws Exception {
                    return new AnsiFetchClause(integer);
                }
            };
        }
    }
}
