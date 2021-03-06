package com.googlecode.lazyrecords.memory;

import com.googlecode.lazyrecords.Definition;
import com.googlecode.totallylazy.Atomic;
import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.collections.PersistentList;
import com.googlecode.totallylazy.collections.PersistentMap;
import com.googlecode.totallylazy.collections.PersistentSortedMap;

import static com.googlecode.totallylazy.Atomic.constructors.atomic;

public class STM implements Atomic<PersistentMap<Definition, PersistentList<PersistentMap<String, String>>>> {
    private final Atomic<PersistentMap<Definition, PersistentList<PersistentMap<String, String>>>> value;

    public STM(PersistentMap<Definition, PersistentList<PersistentMap<String, String>>> value) {
        this.value = atomic(value);
    }

    public STM() {
        this(PersistentSortedMap.constructors.<Definition, PersistentList<PersistentMap<String, String>>>sortedMap());
    }

    @Override
    public Atomic<PersistentMap<Definition, PersistentList<PersistentMap<String, String>>>> modify(Function1<? super PersistentMap<Definition, PersistentList<PersistentMap<String, String>>>, ? extends PersistentMap<Definition, PersistentList<PersistentMap<String, String>>>> callable) {
        return value.modify(callable);
    }

    @Override
    public <R> R modifyReturn(Function1<? super PersistentMap<Definition, PersistentList<PersistentMap<String, String>>>, ? extends Pair<? extends PersistentMap<Definition, PersistentList<PersistentMap<String, String>>>, ? extends R>> function1) {
        return value.modifyReturn(function1);
    }

    @Override
    public PersistentMap<Definition, PersistentList<PersistentMap<String, String>>> value() {
        return value.value();
    }

    public STM snapshot() {
        return new STM(value.value());
    }
}
