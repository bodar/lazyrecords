package com.googlecode.lazyrecords;

import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.predicates.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.predicates.LogicalPredicate;

import static com.googlecode.totallylazy.predicates.Predicates.and;
import static com.googlecode.totallylazy.predicates.Predicates.is;
import static com.googlecode.totallylazy.predicates.Predicates.where;

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

    private Function1<Keyword<?>, Predicate<Record>> asPredicate(final Record record) {
        return keyword -> where(keyword, is(record.get(keyword)));
    }

    public Sequence<Keyword<?>> keywords() {
        return keywords;
    }

    @Override
    public boolean matches(final Record a, final Record b) {
        return call(a).matches(b);
    }
}
