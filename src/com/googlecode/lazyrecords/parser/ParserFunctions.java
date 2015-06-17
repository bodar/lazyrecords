package com.googlecode.lazyrecords.parser;

import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Callers;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Triple;
import com.googlecode.totallylazy.collections.PersistentList;
import com.googlecode.totallylazy.template.Renderer;
import com.googlecode.totallylazy.template.Templates;

import static com.googlecode.totallylazy.Unchecked.cast;
import static com.googlecode.totallylazy.collections.PersistentList.constructors.empty;

public class ParserFunctions {
    private PersistentList<Triple<String, Predicate<?>, Object>> functions = empty();

    public <T> ParserFunctions add(String name, Predicate<? super T> predicate, Function1<? super T, ? extends CharSequence> renderer) {
        functions = functions.append(Triple.<String, Predicate<?>, Object>triple(name, predicate, renderer));
        return this;
    }

    public <T> ParserFunctions add(String name, Predicate<? super T> predicate, Renderer<? super T> renderer) {
        functions = functions.append(Triple.<String, Predicate<?>, Object>triple(name, predicate, renderer));
        return this;
    }

    public Templates addTo(Templates templates) {
        return functions.fold(templates, (accumulator, triple) ->
                accumulator.add(triple.first(), cast(triple.second()), convert(triple.third())));
    }

    private <T> Renderer<T> convert(Object function) {
        if(function instanceof Renderer) return cast(function);
        if(function instanceof Function1) return renderer(cast(function));
        throw new UnsupportedOperationException();
    }

    public static <T> Renderer<T> renderer(Function1<? super T, ? extends CharSequence> callable) {
        return (T instance, Appendable appendable) ->
                appendable.append(Callers.call(callable, instance));
    }

}
