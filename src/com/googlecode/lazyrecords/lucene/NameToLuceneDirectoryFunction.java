package com.googlecode.lazyrecords.lucene;

import com.googlecode.totallylazy.Function;
import com.googlecode.totallylazy.Value;
import org.apache.lucene.store.Directory;

public class NameToLuceneDirectoryFunction implements Value<Function<String, Directory>> {
    private Function<String, Directory> activator;

    public NameToLuceneDirectoryFunction(Function<String, Directory> activator) {
        this.activator = activator;
    }

    @Override
    public Function<String, Directory> value() {
        return activator;
    }
}
