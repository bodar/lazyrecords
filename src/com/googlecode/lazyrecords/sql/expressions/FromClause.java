package com.googlecode.lazyrecords.sql.expressions;

import com.googlecode.lazyrecords.Keyword;

import static java.lang.String.format;

public class FromClause extends TextOnlyExpression{
    public FromClause(Keyword table) {
        super(format("from %s", table));
    }

    public static Expression fromClause(Keyword table) {
        return new FromClause(table);
    }
}
