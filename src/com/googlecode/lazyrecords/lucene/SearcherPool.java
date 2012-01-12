package com.googlecode.lazyrecords.lucene;

import java.io.Closeable;
import java.io.IOException;

public interface SearcherPool extends Closeable{
    int size();

    Searcher searcher() throws IOException;

    void markAsDirty();
}
