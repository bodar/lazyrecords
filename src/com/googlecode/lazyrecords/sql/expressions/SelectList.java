package com.googlecode.lazyrecords.sql.expressions;

import com.googlecode.lazyrecords.Keyword;
import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;

import static com.googlecode.lazyrecords.Keyword.constructors.keyword;
import static com.googlecode.lazyrecords.sql.expressions.DerivedColumn.methods.columnReferences;

public interface SelectList extends Expression {
    Sequence<DerivedColumn> derivedColumns();

    class methods {
        public static Sequence<Keyword<?>> fields(final SelectList list) {
           return fields(list.derivedColumns()).reverse().unique();
        }

        public static Sequence<Keyword<?>> fields(final Sequence<DerivedColumn> columns) {
            return columns.flatMap(column -> {
                if (!column.asClause().isEmpty())
                    return Sequences.<Keyword<?>>one(keyword(removeQuotes(column.asClause().get().alias()), column.forClass()));
                return columnReferences(column).map(asKeyword(column));
            });
        }

        private static Function1<ColumnReference, Keyword<?>> asKeyword(final DerivedColumn column) {
            return columnReference -> keyword(removeQuotes(columnReference.name()), column.forClass());
        }

        public static String removeQuotes(String s) {
            s = s.trim();
            if (s.startsWith("\"")) s = s.substring(1);
            if (s.endsWith("\"")) s = s.substring(0, s.length() - 1);
            return s;
        }
    }
}
