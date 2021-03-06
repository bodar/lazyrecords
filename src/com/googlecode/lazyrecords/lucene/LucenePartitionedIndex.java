package com.googlecode.lazyrecords.lucene;

import com.googlecode.lazyrecords.Definition;
import com.googlecode.totallylazy.*;
import com.googlecode.totallylazy.collections.PersistentMap;
import com.googlecode.totallylazy.functions.Callables;
import com.googlecode.totallylazy.functions.Lazy;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.googlecode.totallylazy.Callers.call;
import static com.googlecode.totallylazy.Closeables.safeClose;
import static com.googlecode.totallylazy.Files.directory;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.io.Zip.unzip;
import static com.googlecode.totallylazy.io.Zip.zip;
import static com.googlecode.totallylazy.collections.PersistentSortedMap.constructors.sortedMap;

public class LucenePartitionedIndex implements Closeable, Persistence, PartitionedIndex {
    private final ConcurrentMap<String, Lazy<LuceneStorage>> partitions = new ConcurrentHashMap<>();
    private final NameToLuceneStorageFunction storageActivator;

    public LucenePartitionedIndex(NameToLuceneStorageFunction storageActivator) {
        this.storageActivator = storageActivator;
    }

    public void close() throws IOException {
        sequence(partitions.values()).map(Callables.<LuceneStorage>call()).each(safeClose());
        partitions.clear();
    }

    @Override
    public LuceneStorage partition(Definition definition) throws IOException {
        return partition(definition.name());
    }

    @Override
    public LuceneStorage partition(final String definition) throws IOException {
        partitions.putIfAbsent(definition, storageFor(definition));
        return call(partitions.get(definition));
    }

    private Lazy<LuceneStorage> storageFor(final String definition) {
        return new Lazy<LuceneStorage>() {
            @Override
            protected LuceneStorage get() throws Exception {
                return storageActivator.getForName(definition);
            }
        };
    }

    @Override
    public PersistentMap<String, LuceneStorage> partitions() {
        final Sequence<Pair<String, Lazy<LuceneStorage>>> partitionsPairs = Maps.pairs(partitions);
        return sortedMap(partitionsPairs.map(Callables.<String, Lazy<LuceneStorage>, LuceneStorage>second(Callables.<LuceneStorage>call())));
    }

    @Override
    public void deleteAll() throws IOException {
        for (Lazy<LuceneStorage> partition : partitions.values()) {
            call(partition).deleteAll();
        }
    }

    @Override
    public void backup(File bgb) throws Exception {
        File destination = tempUnzipLocation();
        Files.delete(destination);

        for (Map.Entry<String, Lazy<LuceneStorage>> entry : partitions.entrySet()) {
            String name = entry.getKey();
            LuceneStorage luceneStorage = call(entry.getValue());
            luceneStorage.backup(directory(destination, name));
        }

        zip(destination, bgb);
        Files.delete(destination);
    }

    @Override
    public void restore(File file) throws Exception {
        File sourceDirectory = unzipIfNeeded(file);
        deleteAll();
        for (File partition : Files.files(sourceDirectory)) {
            String name = partition.getName();
            LuceneStorage luceneStorage = partition(name);
            luceneStorage.restore(directory(sourceDirectory, name));
        }
    }

    private File tempUnzipLocation() {
        return Files.emptyVMDirectory("lucene-index-unzipped");
    }

    private File unzipIfNeeded(File source) throws IOException {
        if (source.isFile()) {
            File unzipped = tempUnzipLocation();
            unzip(source, unzipped);
            return unzipped;
        }
        return source;
    }
}