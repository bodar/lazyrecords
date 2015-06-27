package com.googlecode.lazyrecords.csv;

import com.googlecode.lazyrecords.Keyword;
import com.googlecode.lazyrecords.Record;
import com.googlecode.totallylazy.*;
import com.googlecode.totallylazy.parser.Parser;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import static com.googlecode.lazyrecords.Keyword.constructors.keyword;
import static com.googlecode.lazyrecords.Record.constructors.record;
import static com.googlecode.totallylazy.predicates.Predicates.not;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.parser.Parsers.*;

public interface CsvReader {

    Sequence<Record> read(Reader reader);

    default Sequence<Record> read(String value) {
        return read(new StringReader(value));
    }

    class Grammar {
        public static final char QUOTE = '"';
        public static final char COMMA = ',';

        public static final Parser<String> RAW = characters(Characters.notAmong(",\n\r")).map(Object::toString);
        public static final Parser<String> ESCAPED_QUOTE = isChar(QUOTE).times(2).returns("\"");
        public static final Parser<String> QUOTED = or(characters(not(QUOTE)), ESCAPED_QUOTE).many().
                map(list -> sequence(list).toString("")).between(isChar(QUOTE), isChar(QUOTE));
        public static final Parser<String> TEXT = or(QUOTED, RAW);
        public static final Parser<List<String>> FIELDS = TEXT.sepBy(isChar(COMMA));
        public static final Parser<List<List<String>>> ROW = FIELDS.sepBy(among("\n\r").many());
    }

    enum constructors implements CsvReader {
        csvReader;

        @Override
        public Sequence<Record> read(Reader reader) {
            List<List<String>> allLines = Grammar.ROW.parse(reader).value();
            Sequence<Keyword<String>> keywords = keywords(allLines.get(0));
            return records(keywords, allLines.subList(1, allLines.size()));
        }

        private Sequence<Record> records(final Sequence<Keyword<String>> keywords, Iterable<List<String>> rows) {
            return sequence(rows).map(fields -> record(keywords.zip(fields))).realise();
        }

        private Sequence<Keyword<String>> keywords(Iterable<String> header) {
            return sequence(header).map(name -> (Keyword<String>) keyword(name, String.class)).realise();
        }

    }
}
