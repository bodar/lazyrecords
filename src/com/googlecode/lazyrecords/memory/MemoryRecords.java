package com.googlecode.lazyrecords.memory;

import com.googlecode.lazyrecords.*;
import com.googlecode.lazyrecords.Record;
import com.googlecode.lazyrecords.mappings.StringMappings;
import com.googlecode.totallylazy.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.googlecode.lazyrecords.Definition.functions.sortFields;
import static com.googlecode.totallylazy.Maps.map;
import static com.googlecode.totallylazy.Sequences.sequence;

public class MemoryRecords extends AbstractRecords {
    private final ConcurrentMap<String, List<Map<String, String>>> data = new ConcurrentHashMap<String, List<Map<String, String>>>();
    private final StringMappings mappings;

    public MemoryRecords(StringMappings mappings) {
        this.mappings = mappings;
    }

    public MemoryRecords() {
        this(new StringMappings());
    }

    public Sequence<Record> get(Definition definition) {
        return sequence(recordsFor(definition)).map(asRecord(definition));
    }

    private List<Map<String, String>> recordsFor(Definition definition) {
        data.putIfAbsent(definition.name(), new CopyOnWriteArrayList<>());
        return data.get(definition.name());
    }

    public Number add(final Definition definition, Sequence<Record> records) {
        if (records.isEmpty()) {
            return 0;
        }

        List<Map<String, String>> newRecords = records.map(sortFields(definition)).map(asMap(definition)).toList();
        List<Map<String, String>> list = recordsFor(definition);
        list.addAll(newRecords);

        return newRecords.size();
    }

    public Number remove(Definition definition, Predicate<? super Record> predicate) {
        List<Map<String, String>> matches = get(definition).
                filter(predicate).
                <Value<Map<String, String>>>unsafeCast().
                map(Callables.<Map<String, String>>value()).
                toList();

        recordsFor(definition).removeAll(matches);

        return matches.size();
    }

    private Function1<Map<String, String>, Record> asRecord(final Definition definition) {
        return data1 -> SourceRecord.record(data1, definition.fields().map(values(data1)));
    }

    private Function1<Keyword<?>, Pair<Keyword<?>, Object>> values(final Map<String, String> map) {
        return keyword -> Pair.<Keyword<?>, Object>pair(keyword, mappings.toValue(keyword.forClass(), map.get(keyword.name())));
    }

    private Function1<Record, Map<String, String>> asMap(final Definition definition) {
        return record -> map(definition.fields().map(values(record)));
    }

    private Function1<Keyword<?>, Pair<String, String>> values(final Record record) {
        return keyword -> Pair.pair(keyword.name(), mappings.toString(keyword.forClass(), record.get(keyword)));
    }
}
