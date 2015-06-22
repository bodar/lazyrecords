package com.googlecode.lazyrecords.lucene;

import com.googlecode.lazyrecords.Named;
import com.googlecode.lazyrecords.Record;
import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.annotations.multimethod;
import com.googlecode.totallylazy.comparators.AscendingComparator;
import com.googlecode.totallylazy.comparators.CompositeComparator;
import com.googlecode.totallylazy.comparators.DescendingComparator;
import com.googlecode.totallylazy.multi;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

import java.util.Comparator;

import static com.googlecode.totallylazy.Sequences.sequence;

public class Sorting {
    private static multi multi;
    public static Sort sort(Comparator<? super Record> comparator) {
        if(multi == null) multi = new multi(){};
        return multi.<Sort>methodOption(comparator).getOrThrow(new UnsupportedOperationException("Unsupported comparator " + comparator));
    }

    @multimethod public static Sort sort(AscendingComparator<? super Record, ?> comparator) {
        return sortBy(name(comparator.callable()), false);
    }

    @multimethod public static Sort sort(DescendingComparator<? super Record, ?> comparator) {
        return sortBy(name(comparator.callable()), true);
    }

    @multimethod public static Sort sort(CompositeComparator<? super Record> comparator) {
        Sequence<SortField> sortFields = comparator.comparators().map(sort()).flatMap(sortFields());
        return new Sort(sortFields.toArray(SortField.class));
    }

    private static Function1<Sort, Iterable<SortField>> sortFields() {
        return sort -> sequence(sort.getSort());
    }

    private static Function1<Comparator<? super Record>, Sort> sort() {
        return Sorting::sort;
    }

    private static Sort sortBy(String name, boolean reverse) {
        return new Sort(new SortField(name, SortField.Type.STRING, reverse));
    }

    private static String name(Function1<?, ?> callable) {
        if (callable instanceof Named) {
            return ((Named) callable).name();
        }
        throw new UnsupportedOperationException("Unsupported reducer " + callable);
    }
}
