package com.googlecode.lazyrecords;

import com.googlecode.totallylazy.predicates.Predicates;
import org.junit.Test;

import static com.googlecode.lazyrecords.Keyword.constructors.keyword;
import static com.googlecode.totallylazy.predicates.Predicates.where;
import static com.googlecode.totallylazy.matchers.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class KeywordsTest {
    @Test
    public void supportsToString() throws Exception {
        assertThat(where(keyword("Some column", Integer.class), Predicates.is(1)).toString(), is("where Some column is 1"));
    }
}
