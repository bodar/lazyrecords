package com.googlecode.lazyrecords.sql.expressions;

import com.googlecode.totallylazy.Function1;

public interface FetchClause extends Expression {
    int number();

    class functions {
        public static Function1<FetchClause, Integer> number() {
            return fetchClause -> fetchClause.number();
        }

        public static Function1<Integer, FetchClause> fetchClause() {
            return integer -> new AnsiFetchClause(integer);
        }
    }
}
