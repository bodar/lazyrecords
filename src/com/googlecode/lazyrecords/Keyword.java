package com.googlecode.lazyrecords;

import com.googlecode.totallylazy.*;
import com.googlecode.totallylazy.functions.Binary;
import com.googlecode.totallylazy.functions.Callables;
import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.functions.Unary;

import static com.googlecode.totallylazy.functions.Functions.returns1;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Strings.equalIgnoringCase;

public interface Keyword<T> extends Named, Metadata<Keyword<T>>, Function1<Record, T>, GenericType<T>, Comparable<Keyword<T>> {
    Record metadata();

    public String toString();

    class constructors {
        public static ImmutableKeyword<Object> keyword(String value) {
            return new ImmutableKeyword<Object>(value, Object.class);
        }

        public static <T> ImmutableKeyword<T> keyword(String value, Class<? extends T> aClass) {
            return new ImmutableKeyword<T>(value, aClass);
        }

        public static <T> ImmutableKeyword<T> keyword(Keyword<? extends T> keyword) {
            return keyword(keyword.name(), keyword.forClass()).metadata(keyword.metadata());
        }

        public static <T> CompositeKeyword<T> compose(Binary<T> combiner, Sequence<? extends Keyword<T>> keywords) {
            return CompositeKeyword.compose(combiner, keywords);
        }
    }

    class methods {
        public static Keyword<Object> matchKeyword(String name, Sequence<? extends Keyword<?>> definitions) {
            return matchKeyword(name, definitions, Keyword.functions.name());
        }

        public static Keyword<Object> matchKeyword(String shortName, Sequence<? extends Keyword<?>> definitions, Function1<Keyword<?>, String> extractor) {
            return definitions.<Keyword<Object>>unsafeCast().find(where(extractor, equalIgnoringCase(shortName))).getOrElse(Keyword.constructors.keyword(shortName));
        }

        public static boolean equalTo(Keyword<?> keyword, Keyword<?> other) {
            return keyword.name().equalsIgnoreCase(other.name());
        }

        public static Sequence<Keyword<?>> keywords(Sequence<Record> results) {
            return results.flatMap(Record.functions.keywords).unique().realise();
        }
    }

    class functions {
        public static Function1<Keyword<?>, String> name = Named::name;

        public static Function1<Keyword<?>, String> name() { return name; }

        public static <T> Function1<Keyword<?>, T> metadata(final Keyword<T> metadataKey) {
            return keyword -> keyword.metadata().get(metadataKey);
        }

        public static <T> Unary<Keyword<T>> metadata(final Record metadata) {
            return keyword -> keyword.metadata(metadata);
        }

        public static <T, M> Unary<Keyword<T>> metadata(final Keyword<M> name, final M value) {
            return keyword -> keyword.metadata(name, value);
        }

        public static Unary<Keyword<?>> replace(Keyword<?> from, Keyword<?> to) {
            return Callables.<Keyword<?>>replace(Predicates.<Keyword<?>>is(from), returns1(to));
        }
    }
}