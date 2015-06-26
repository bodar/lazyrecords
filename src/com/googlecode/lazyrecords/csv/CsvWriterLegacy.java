package com.googlecode.lazyrecords.csv;

import com.googlecode.lazyrecords.Keyword;
import com.googlecode.lazyrecords.Record;
import com.googlecode.lazyrecords.RecordTo;
import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.functions.Curried2;
import com.googlecode.totallylazy.Iterators;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Sequence;

import java.io.Writer;
import java.util.Iterator;

import static com.googlecode.totallylazy.functions.Callables.toString;

public class CsvWriterLegacy {
    private static final String FIELD_SEPARATOR = ",";
    private static final char ROW_SEPARATOR = '\n';

    public static void writeTo(Iterator<Record> records, Writer writer, Sequence<? extends Keyword<?>> fields) {
        Iterators.fold(Iterators.cons(headers(fields), Iterators.map(records, rowToString(fields))), writer, writeLine());
    }

    private static Curried2<Writer, String, Writer> writeLine() {
        return (writer, line) -> writer.append(line).append(ROW_SEPARATOR);
    }

    private static RecordTo<String> rowToString(final Sequence<? extends Keyword<?>> fields) {
        return new RecordTo<String>() {
            @Override
            public String call(final Record record) throws Exception {
                return fields.map(fieldToString(record)).map(escapeSpecialCharacters()).toString(FIELD_SEPARATOR);
            }
        };
    }

    private static Function1<Keyword<?>, String> fieldToString(final Record record) {
        return keyword -> Option.option(record.get(keyword)).map(toString).getOrElse("");
    }

    private static Function1<String, String> escapeSpecialCharacters() {
        return recordValue -> {
            recordValue = recordValue.replace('\n', ' ');
            if (recordValue.contains(",")) {
                return '"' + recordValue + '"';
            }
            return recordValue;
        };
    }

    private static String headers(Sequence<? extends Keyword<?>> fields) {
        return fields.map(Keyword.functions.name).toString(FIELD_SEPARATOR);
    }
}