package com.googlecode.lazyrecords.sql.expressions;

import com.googlecode.totallylazy.Function;
import com.googlecode.totallylazy.Sequence;

import static com.googlecode.lazyrecords.sql.expressions.Expressions.textOnly;

public interface OrderByClause extends Expression {
    TextOnlyExpression orderBy = textOnly("order by");
    Sequence<SortSpecification> sortSpecifications();

    class functions {
        public static Function<OrderByClause, Sequence<SortSpecification>> sortSpecifications = new Function<OrderByClause, Sequence<SortSpecification>>() {
            @Override
            public Sequence<SortSpecification> call(OrderByClause orderByClause) throws Exception {
                return orderByClause.sortSpecifications();
            }
        };
    }
}
