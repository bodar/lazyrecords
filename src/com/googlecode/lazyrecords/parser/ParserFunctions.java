package com.googlecode.lazyrecords.parser;

import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.functions.Function1;
import com.googlecode.totallylazy.Callers;
import com.googlecode.totallylazy.collections.PersistentList;
import com.googlecode.totallylazy.template.Renderer;
import com.googlecode.totallylazy.template.Templates;

import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Unchecked.cast;
import static com.googlecode.totallylazy.collections.PersistentList.constructors.empty;

public class ParserFunctions {
    private PersistentList<Pair<String, Renderer<Object>>> renderers = empty();

    public <T> ParserFunctions add(String name, Function1<? super T, ? extends CharSequence> renderer) {
        return add(name, renderer(cast(renderer)));
    }

    public ParserFunctions add(String name, Renderer<Object> renderer) {
        renderers = renderers.append(pair(name, renderer));
        return this;
    }

    public Templates addTo(Templates templates) {
        return renderers.fold(templates, (accumulator, pair) ->
                accumulator.add(pair.first(), pair.second()));
    }

    public static <T> Renderer<Object> renderer(Function1<Object, ? extends CharSequence> callable) {
        return (Object instance, Appendable appendable) ->
                appendable.append(Callers.call(callable, instance));
    }

}
