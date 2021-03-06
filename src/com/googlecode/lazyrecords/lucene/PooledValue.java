package com.googlecode.lazyrecords.lucene;

import com.googlecode.totallylazy.functions.Block;
import com.googlecode.totallylazy.functions.Function1;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static com.googlecode.totallylazy.Closeables.safeClose;

public class PooledValue implements Closeable {
    private final Searcher searcher;
    private final LuceneSearcher luceneSearcher;
    private volatile boolean dirty = false;
    private final AtomicInteger checkoutCount;

    PooledValue(Searcher searcher, LuceneSearcher luceneSearcher) {
        this.searcher = searcher;
        this.luceneSearcher = luceneSearcher;
        checkoutCount = new AtomicInteger(1);
    }

    public static Function1<PooledValue, Boolean> isDirty() {
        return PooledValue::dirty;
    }

    public boolean dirty() {
        return dirty;
    }

    public void dirty(boolean value) {
        this.dirty = value;
    }

    public static Function1<PooledValue, Searcher> checkoutValue() {
        return PooledValue::checkout;
    }

    public static Function1<PooledValue, Searcher> searcher() {
        return pooledValue -> pooledValue.searcher;
    }

    public int checkoutCount() {
        return checkoutCount.get();
    }

    private Searcher checkout() {
        checkoutCount.incrementAndGet();
        return searcher;
    }

    public static Function1<PooledValue, Integer> theCheckoutCount() {
        return PooledValue::checkoutCount;
    }

    public int checkin() {
        return checkoutCount.decrementAndGet();
    }

    public static Block<PooledValue> markAsDirty() {
        return pooledValue -> pooledValue.dirty(true);
    }

    @Override
    public void close() throws IOException {
        safeClose(luceneSearcher);
    }
}
