package com.googlecode.lazyrecords.sql.expressions;

import com.googlecode.lazyrecords.Aggregate;
import com.googlecode.lazyrecords.Aggregates;
import com.googlecode.lazyrecords.Keyword;
import com.googlecode.lazyrecords.Record;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.predicates.Predicate;
import com.googlecode.totallylazy.functions.Reducer;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.Unchecked;

import static com.googlecode.lazyrecords.Keyword.constructors.keyword;
import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.predicates.Predicates.and;
import static com.googlecode.totallylazy.Sequences.join;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Unchecked.cast;

public class SelectBuilder {
    public static Predicate<? super Record> combine(final Option<Predicate<? super Record>> previous, Predicate<? super Record> predicate) {
        if (previous.isEmpty()) return predicate;
        return and(previous.get(), predicate);
    }

    public static Sequence<Keyword<?>> countStar() {
        Aggregate<?, Number> recordCount = Aggregate.count(keyword("*", Long.class)).as("record_count");
        return Sequences.<Keyword<?>>sequence(recordCount);
    }

    public static Sequence<Keyword<?>> aggregates(Reducer<?,?> callable, Sequence<Keyword<?>> fields) {
        if (callable instanceof Aggregates) {
            Aggregates aggregates = (Aggregates) callable;
            return aggregates.value().unsafeCast();
        }
        Keyword<Object> cast = column(fields);
        Aggregate<Object, Object> aggregate = Aggregate.aggregate(Unchecked.<Reducer<Object, Object>>cast(callable), cast, cast.forClass());
        return Sequences.<Keyword<?>>sequence(aggregate);
    }

    public static Keyword<Object> column(Sequence<Keyword<?>> fields) {
        if (fields.size() == 1) return cast(fields.head());
        return cast(keyword("*", Long.class));
    }


}
