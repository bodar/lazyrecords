package com.googlecode.lazyrecords.simpledb;

import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpledb.model.SelectResult;
import com.googlecode.lazyrecords.Keyword;
import com.googlecode.lazyrecords.Logger;
import com.googlecode.lazyrecords.Loggers;
import com.googlecode.lazyrecords.Record;
import com.googlecode.lazyrecords.SelectFunction;
import com.googlecode.lazyrecords.mappings.StringMappings;
import com.googlecode.lazyrecords.sql.expressions.Expression;
import com.googlecode.lazyrecords.sql.expressions.ExpressionBuilder;
import com.googlecode.lazyrecords.sql.expressions.Expressions;
import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.Computation;
import com.googlecode.totallylazy.functions.Lazy;
import com.googlecode.totallylazy.Maps;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.Unchecked;
import com.googlecode.totallylazy.Value;

import java.util.Comparator;
import java.util.Iterator;

import static com.googlecode.totallylazy.Pair.pair;

public class SimpleDBSequence<T> extends Sequence<T> {
    private final AmazonSimpleDB sdb;
    private final ExpressionBuilder builder;
    private final StringMappings mappings;
    private final Function1<? super Item, T> itemToRecord;
    private final Logger logger;
    private final boolean consistentRead;
    private final Value<Iterable<T>> data;

    public SimpleDBSequence(AmazonSimpleDB sdb, final ExpressionBuilder builder, StringMappings mappings, Function1<? super Item, T> itemToRecord, Logger logger, boolean consistentRead) {
        this.sdb = sdb;
        this.builder = builder;
        this.mappings = mappings;
        this.itemToRecord = itemToRecord;
        this.logger = logger;
        this.consistentRead = consistentRead;
        this.data = Lazy.lazy(() -> Computation.memorise(iterator(builder)));
    }

    public Iterator<T> iterator() {
        return data.value().iterator();
    }

    private Iterator<T> iterator(final Expression expression) {
        String selectExpression = Expressions.toString(expression, value());
        logger.log(Maps.map(pair(Loggers.TYPE, Loggers.SIMPLE_DB), pair(Loggers.EXPRESSION, selectExpression)));
        return iterator(new SelectRequest(selectExpression, consistentRead)).map(itemToRecord).iterator();
    }

    private Function1<Object, Object> value() {
        return value -> mappings.toString(value.getClass(), value);
    }

    private Sequence<Item> iterator(final SelectRequest selectRequest) {
        SelectResult result = sdb.select(selectRequest);
        Sequence<Item> items = Sequences.sequence(result.getItems());
        String nextToken = result.getNextToken();
        if(nextToken != null){
            return items.join(iterator(selectRequest.withNextToken(nextToken)));
        }
        return items;
    }

    @Override
    public Sequence<T> filter(Predicate<? super T> predicate) {
        return new SimpleDBSequence<T>(sdb, builder.filter(Unchecked.<Predicate<Record>>cast(predicate)), mappings, itemToRecord, logger, consistentRead);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S> Sequence<S> map(final Function1<? super T, ? extends S> callable) {
        Function1 raw = callable;
        if (raw instanceof Keyword) {
            final Keyword<S> keyword = (Keyword<S>) raw;
            return new SimpleDBSequence<S>(sdb, builder.select(keyword), mappings, itemToValue(keyword), logger, consistentRead);
        }
        if (raw instanceof SelectFunction) {
            return (Sequence<S>) new SimpleDBSequence(sdb, builder.select(((SelectFunction) raw).keywords()), mappings, itemToRecord, logger, consistentRead);
        }
        logger.log(Maps.map(pair(Loggers.TYPE, Loggers.SIMPLE_DB), pair(Loggers.MESSAGE, "Unsupported function passed to 'map', moving computation to client"), pair(Loggers.FUNCTION, callable)));
        return super.map(callable);
    }

    private <S> Function1<Item, S> itemToValue(final Keyword<S> keyword) {
        return item -> ((Record) itemToRecord.call(item)).get(keyword);
    }

    @Override
    public Sequence<T> sortBy(Comparator<? super T> comparator) {
        try {
            return new SimpleDBSequence<T>(sdb, builder.orderBy(Unchecked.<Comparator<Record>>cast(comparator)), mappings, itemToRecord, logger, consistentRead);
        } catch (UnsupportedOperationException ex) {
            logger.log(Maps.map(pair(Loggers.TYPE, Loggers.SIMPLE_DB), pair(Loggers.MESSAGE, "Unsupported comparator passed to 'sortBy', moving computation to client"), pair(Loggers.COMPARATOR, comparator)));
            return super.sortBy(comparator);
        }
    }
}
