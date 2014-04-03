package com.googlecode.lazyrecords.lucene;

import com.googlecode.lazyrecords.Keyword;
import com.googlecode.lazyrecords.Logger;
import com.googlecode.lazyrecords.Record;
import com.googlecode.lazyrecords.RecordsContract;
import com.googlecode.lazyrecords.lucene.mappings.LuceneMappings;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.matchers.Matchers;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import static com.googlecode.lazyrecords.Grammar.is;
import static com.googlecode.lazyrecords.Grammar.keyword;
import static com.googlecode.lazyrecords.Grammar.where;
import static com.googlecode.lazyrecords.RecordsContract.People.age;
import static com.googlecode.lazyrecords.RecordsContract.People.firstName;
import static com.googlecode.lazyrecords.RecordsContract.People.lastName;
import static com.googlecode.lazyrecords.RecordsContract.People.people;
import static com.googlecode.totallylazy.Files.emptyVMDirectory;
import static com.googlecode.totallylazy.matchers.IterableMatcher.hasExactly;
import static org.hamcrest.MatcherAssert.assertThat;

public class LuceneRecordsTest extends RecordsContract<LuceneRecords> {
    public static final Version VERSION = Version.LUCENE_45;
    public static final Analyzer ANALYZER = new StandardAnalyzer(VERSION);
    private Directory directory;
    private LuceneStorage storage;
    private File file;

    @Override
    protected LuceneRecords createRecords() throws Exception {
        file = emptyVMDirectory("totallylazy");
        directory = new NoSyncDirectory(file);
        storage = new OptimisedStorage(directory, Version.LUCENE_45, new StringPhraseAnalyzer(), IndexWriterConfig.OpenMode.CREATE_OR_APPEND, new LucenePool(directory));
        return luceneRecords(logger);
    }

    private LuceneRecords luceneRecords(Logger logger1) throws IOException {
        return new LuceneRecords(storage, new LuceneMappings(), logger1, new LowerCasingQueryVisitor());
    }

    @After
    public void cleanUp() throws Exception {
        super.cleanUp();
        records.close();
        storage.close();
        directory.close();
    }

    @Override
    @Ignore("Still thinking about lexical representation of BigDecimal")
    public void supportsBigDecimal() throws Exception {
    }

    @Test
    public void canQueryIndexDirectly() throws Exception {
        QueryParser parser = new QueryParser(VERSION, null, ANALYZER);
        Sequence<Record> results = records.query(parser.parse("type:people +firstName:da*"), Sequences.<Keyword<?>>sequence(lastName));
        assertThat(results.map(lastName), hasExactly("bodart"));
    }

    @Test
    public void memorisesAndThereforeOnlyExecutesQueryOnce() throws Exception {
        Sequence<Record> result = records.get(people).sortBy(age);
        Record head = result.head();
        Sequence<Map<String, ?>> logs = memory.data();
        assertThat(head, Matchers.is(result.head())); // Check iterator
        assertThat(memory.data(), Matchers.is(logs)); // Check queries
    }

    @Override
    @Ignore
    public void supportsConcatenationDuringFiltering() throws Exception {
    }

    @Override
    @Ignore
    public void supportsAliasingAKeywordDuringFilter() throws Exception {
    }

    @Override
    @Ignore
    public void canFullyQualifyAKeywordDuringFiltering() throws Exception {
    }

    @Test
    public void searchShouldBeCaseInsensitive() throws Exception {
        Sequence<Record> result = records.get(people).filter(where(keyword("firstName", String.class), is("bOB")));
        assertThat(result.size(), Matchers.is(1));
        assertThat(result.head().get(firstName), Matchers.is("Bob"));
    }

    public static class StringPhraseAnalyzer extends Analyzer  {
        protected TokenStreamComponents createComponents (String fieldName, Reader reader) {
            Tokenizer tok = new KeywordTokenizer(reader);
            TokenFilter filter = new LowerCaseFilter(Version.LUCENE_45, tok);
            filter = new TrimFilter(Version.LUCENE_45, filter);
            return new TokenStreamComponents(tok, filter);
        }
    }
}
