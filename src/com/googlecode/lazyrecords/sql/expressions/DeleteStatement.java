package com.googlecode.lazyrecords.sql.expressions;

import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.lazyrecords.Keyword;
import com.googlecode.lazyrecords.Record;

import static com.googlecode.lazyrecords.sql.expressions.Expressions.textOnly;
import static com.googlecode.lazyrecords.sql.expressions.FromClause.fromClause;
import static com.googlecode.lazyrecords.sql.expressions.WhereClause.whereClause;

public class DeleteStatement extends CompoundExpression {
    public DeleteStatement(Keyword recordName, Option<Predicate<? super Record>> predicate) {
        super(
                textOnly("delete"),
                fromClause(recordName),
                whereClause(predicate)
                );
    }

    public static Expression deleteStatement(Keyword recordName, Predicate<? super Record> predicate) {
        return new DeleteStatement(recordName, Option.<Predicate<? super Record>>some(predicate));
    }

    public static Expression deleteStatement(Keyword recordName) {
        return new DeleteStatement(recordName, Option.<Predicate<? super Record>>none());
    }
}
