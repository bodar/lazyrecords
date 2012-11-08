package com.googlecode.lazyrecords.lucene;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Sequence;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;

import java.io.Closeable;
import java.io.File;
import java.io.Flushable;
import java.io.IOException;

public interface LuceneStorage extends Closeable, Flushable{
    Number add(Sequence<Document> documents) throws IOException;

    Number delete(Query query) throws IOException;

    void deleteNoCount(Query query) throws IOException;

    void deleteAll() throws IOException;

    int count(Query query) throws IOException;

    <T> T search(Callable1<Searcher, T> callable) throws IOException;

    Searcher searcher() throws IOException;

    void backup(File destination) throws Exception;

    void restore(File file) throws Exception;
}
