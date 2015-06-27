package com.googlecode.lazyrecords.parser;

import com.googlecode.lazyrecords.Keyword;
import com.googlecode.lazyrecords.mappings.StringMappings;
import org.junit.Test;

import static com.googlecode.lazyrecords.Keyword.constructors.keyword;
import static com.googlecode.totallylazy.Assert.assertThat;
import static com.googlecode.totallylazy.Assert.assertTrue;
import static com.googlecode.totallylazy.predicates.Predicates.is;
import static com.googlecode.totallylazy.predicates.Predicates.not;
import static com.googlecode.totallylazy.Sequences.sequence;

public class GrammarTest {
    final Keyword<String> name = keyword("name", String.class);
    final Keyword<String> age = keyword("age", String.class);
    final Keyword<String> id = keyword("id", String.class);
    final Grammar grammar = new Grammar(sequence(name, age, id), new StringMappings());
    
    @Test
    public void quotes() throws Exception{
        assertThat(Grammar.QUOTED_TEXT.parse("\"foo bar\"").value(), is("foo bar"));
    }

    @Test
    public void supportsDateTime() throws Exception {
        assertTrue(grammar.DATE_AND_TIME.parse("2001/01/10 03:15:59").success());
    }
}
