package com.googlecode.lazyrecords;

import com.googlecode.totallylazy.BinaryPredicate;
import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Predicate;

public interface Joiner extends BinaryPredicate<Record>, Function1<Record, Predicate<Record>> {
}
