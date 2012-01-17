package com.googlecode.lazyrecords.sql.expressions;

import com.googlecode.lazyrecords.Keywords;
import com.googlecode.lazyrecords.RecordName;
import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Callables;
import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.callables.CountNotNull;
import com.googlecode.lazyrecords.Aggregate;
import com.googlecode.lazyrecords.Aggregates;
import com.googlecode.lazyrecords.ImmutableKeyword;
import com.googlecode.lazyrecords.Keyword;
import com.googlecode.lazyrecords.Record;

import java.util.Comparator;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.lazyrecords.Aggregate.aggregate;
import static com.googlecode.lazyrecords.Keywords.keyword;
import static com.googlecode.lazyrecords.sql.expressions.SelectExpression.selectExpression;
import static com.googlecode.lazyrecords.sql.expressions.SetQuantifier.ALL;
import static com.googlecode.lazyrecords.sql.expressions.SetQuantifier.DISTINCT;

public class SelectBuilder implements Expressible, Callable<Expression> {
    public static final Keyword<Object> STAR = keyword("*");
    public static final Sequence<Keyword<?>> ALL_COLUMNS = Sequences.<Keyword<?>>sequence(STAR);
    private final SetQuantifier setQuantifier;
    private final Sequence<Keyword<?>> select;
    private final RecordName table;
    private final Option<Predicate<? super Record>> where;
    private final Option<Comparator<? super Record>> comparator;

    private SelectBuilder(SetQuantifier setQuantifier, Sequence<Keyword<?>> select, RecordName table, Option<Predicate<? super Record>> where, Option<Comparator<? super Record>> comparator) {
        this.setQuantifier = setQuantifier;
        this.select = select.isEmpty() ? ALL_COLUMNS : select;
        this.table = table;
        this.where = where;
        this.comparator = comparator;
    }

    public SelectExpression call() throws Exception {
        return build();
    }

    public SelectExpression express() {
        return build();
    }

    public SelectExpression build() {
        return selectExpression(setQuantifier, select, table, where, comparator);
    }

    @Override
    public String toString() {
        return build().toString();
    }

    public Sequence<Keyword<?>> select() {
        return select;
    }

    public static SelectBuilder from(RecordName table) {
        return new SelectBuilder(ALL, ALL_COLUMNS, table, Option.<Predicate<? super Record>>none(), Option.<Comparator<? super Record>>none());
    }

    public SelectBuilder select(Keyword<?>... columns) {
        return select(sequence(columns));
    }

    public SelectBuilder select(Sequence<Keyword<?>> columns) {
        return new SelectBuilder(setQuantifier, columns, table, where, comparator);
    }

    public SelectBuilder where(Predicate<? super Record> predicate) {
        return new SelectBuilder(setQuantifier, select, table, Option.<Predicate<? super Record>>some(predicate), comparator);
    }

    public SelectBuilder orderBy(Comparator<? super Record> comparator) {
        return new SelectBuilder(setQuantifier, select, table, where, Option.<Comparator<? super Record>>some(comparator));
    }

    public SelectBuilder count() {
        Aggregate<Number, Number> aggregate = Aggregate.aggregate(CountNotNull.count(), keyword("*", Number.class));
        Sequence<Keyword<?>> sequence = Sequences.<Keyword<?>>sequence(aggregate.as(keyword("record_count", Number.class)));
        return new SelectBuilder(setQuantifier, sequence, table, where, comparator);
    }

    public SelectBuilder distinct() {
        return new SelectBuilder(DISTINCT, select, table, where, comparator);
    }

    @SuppressWarnings("unchecked")
    public SelectBuilder reduce(Callable2 callable) {
        if (callable instanceof Aggregates) {
            Aggregates aggregates = (Aggregates) callable;
            return new SelectBuilder(setQuantifier, aggregates.value().map(evil()), table, where, comparator);
        }
        Aggregate aggregate = Aggregate.aggregate(callable, select().head());
        return new SelectBuilder(setQuantifier, Sequences.<Keyword<?>>sequence(aggregate), table, where, comparator);
    }

    private Function1<Aggregate<?, ?>, Keyword<?>> evil() {
        return new Function1<Aggregate<?, ?>, Keyword<?>>() {
            @Override
            public Keyword<?> call(Aggregate<?, ?> aggregate) throws Exception {
                return aggregate;
            }
        };
    }
}
