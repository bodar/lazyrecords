package com.googlecode.lazyrecords;

import com.googlecode.totallylazy.Function;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.predicates.LogicalPredicate;

import static com.googlecode.totallylazy.Predicates.and;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.where;

public class Using implements Joiner {
    private final Sequence<Keyword<?>> keywords;

    protected Using(Sequence<Keyword<?>> keywords) {
        this.keywords = keywords;
    }

    public static Using using(Keyword<?>... keyword) {
        return new Using(Sequences.sequence(keyword));
    }

    public static Using using(Sequence<Keyword<?>> keyword) {
        return new Using(keyword);
    }

    public LogicalPredicate<Record> call(Record record) {
        return and(keywords.map(asPredicate(record)).toArray(Predicate.class));
    }

    private Function<Keyword<?>, Predicate<Record>> asPredicate(final Record record) {
        return new Function<Keyword<?>, Predicate<Record>>() {
            public Predicate<Record> call(Keyword<?> keyword) throws Exception {
                return where(keyword, is(record.get(keyword)));
            }
        };
    }

    public Sequence<Keyword<?>> keywords() {
        return keywords;
    }

    @Override
    public boolean matches(final Record a, final Record b) {
        return call(a).matches(b);
    }
}
