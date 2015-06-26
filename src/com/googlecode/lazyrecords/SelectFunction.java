package com.googlecode.lazyrecords;

import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Unchecked;

import static com.googlecode.totallylazy.Sequences.sequence;

public class SelectFunction implements Function1<Record, Record> {
    private final Sequence<Keyword<?>> keywords;

    protected SelectFunction(Sequence<Keyword<?>> keywords) {
        this.keywords = keywords;
    }

    public Record call(final Record source) throws Exception {
        return keywords.fold(Record.constructors.record(), (record, keyword) -> record.set(Unchecked.<Keyword<Object>>cast(keyword), keyword.call(source)));
    }

    public Sequence<Keyword<?>> keywords() {
        return keywords;
    }

    public static Function1<? super Record, Record> select(final Keyword<?>... keywords) {
        return new SelectFunction(sequence(keywords));
    }

    public static Function1<? super Record, Record> select(final Sequence<Keyword<?>> keywords) {
        return new SelectFunction(keywords);
    }

}
