package com.googlecode.lazyrecords.parser;

import com.googlecode.lazyrecords.Keyword;
import com.googlecode.lazyrecords.Record;
import com.googlecode.lazyrecords.mappings.StringMappings;
import com.googlecode.totallylazy.*;
import com.googlecode.totallylazy.parser.Parse;
import com.googlecode.totallylazy.parser.Parser;
import com.googlecode.totallylazy.parser.Parsers;
import com.googlecode.totallylazy.predicates.OrPredicate;
import com.googlecode.totallylazy.time.Dates;
import com.googlecode.totallylazy.time.Seconds;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import static com.googlecode.totallylazy.Predicates.*;
import static com.googlecode.totallylazy.Strings.contains;
import static com.googlecode.totallylazy.Strings.*;
import static com.googlecode.totallylazy.parser.Parsers.*;
import static java.util.Calendar.MILLISECOND;

@SuppressWarnings("unchecked")
public class Grammar {
    final Sequence<? extends Keyword<?>> keywords;
    final StringMappings mappings;
    final Parser<Predicate<Record>> GROUP = Parsers.lazy(new Callable<Parse<Predicate<Record>>>() {
        @Override
        public Parse<Predicate<Record>> call() throws Exception {
            return PARTS.between(ws('('), ws(')'));
        }
    });

    Grammar(final Sequence<? extends Keyword<?>> keywords, StringMappings mappings) {
        this.keywords = keywords;
        this.mappings = mappings;
    }

    public static Parser<Predicate<Record>> PARSER(final Sequence<? extends Keyword<?>> keywords, final StringMappings mappings) {
        return new Grammar(keywords, mappings).PARTS;
    }

    public Parser<Predicate<Record>> VALUE_ONLY = VALUE_PREDICATES.map(new Callable1<List<Pair<String, Callable1<Object, Predicate>>>, Predicate<Record>>() {
        @Override
        public Predicate<Record> call(List<Pair<String, Callable1<Object, Predicate>>> pairs) throws Exception {
            List<Predicate<Record>> predicates = new ArrayList<>();
            for (final Keyword<?> keyword : keywords) {
                predicates.add(toPredicate(keyword, pairs));
            }
            return OrPredicate.or(predicates);
        }
    });

    public final Parser<Predicate<Record>> NAME_AND_VALUE = Parsers.tuple(NAME, SEPARATORS, VALUE_PREDICATES).map(new Callable1<Triple<String, Void, List<Pair<String, Callable1<Object, Predicate>>>>, Predicate<Record>>() {
        @Override
        public Predicate<Record> call(Triple<String, Void, List<Pair<String, Callable1<Object, Predicate>>>> triple) throws Exception {
            final String name = triple.first();
            final List<Pair<String, Callable1<Object, Predicate>>> values = triple.third();
            return toPredicate(Keyword.methods.matchKeyword(name, keywords), values);
        }
    });

    public final Parser<Predicate<Record>> NAME_AND_WILDCARD = Parsers.tuple(NAME, SEPARATORS, WILDCARD).map(triple -> Predicates.all());

    public Parser<Predicate<Record>> PARTS = Parsers.or(GROUP, NAME_AND_VALUE, NAME_AND_WILDCARD, VALUE_ONLY).prefix(NEGATION).infixLeft(OR.or(AND));

    Predicate<Record> toPredicate(final Keyword<?> keyword, final List<Pair<String, Callable1<Object, Predicate>>> values) throws Exception {
        List<Predicate<Record>> valuesPredicates = new ArrayList<>();
        for (Pair<String, Callable1<Object, Predicate>> pair : values) {
            try {
                Object actualValue = mappings.toValue(keyword.forClass(), pair.first());
                Predicate<Record> where = where(keyword, pair.second().call(actualValue));
                valuesPredicates.add(where);
            } catch (Exception ignored) {
            }
        }
        return OrPredicate.or(valuesPredicates);
    }

    public static Parser<Void> ws(char value) {
        return Parsers.wsChar(value).ignore();
    }

    public static Parser<Void> ws(String value) {
        return Parsers.ws(Parsers.string(value)).ignore();
    }

    public static final Parser<String> DATE = Parsers.or(pattern("\\d{4}/\\d{1,2}/\\d{1,2}", "yyyyMMdd"),
            pattern("\\d{1,2}/\\d{1,2}/\\d{4}", "ddMMyyyy"),
            pattern("\\d{1,2}/\\d{1,2}/\\d{2}", "ddMMyy"));
    public static final String TIME = " \\d{1,2}:\\d{2}:\\d{2}";
    public static final Parser<String> DATE_AND_TIME = Parsers.or(pattern("\\d{4}/\\d{1,2}/\\d{1,2}" + TIME, "yyyyMMdd HH:mm:ss"),
            pattern("\\d{1,2}/\\d{1,2}/\\d{4}" + TIME, "ddMMyyyy HH:mm:ss"),
            pattern("\\d{1,2}/\\d{1,2}/\\d{2}" + TIME, "ddMMyy HH:mm:ss"));
    // TODO Fix
    public static final Parser<String> DATE_AND_TIME_BROKEN = DATE.followedBy(isChar('T')).followedBy(pattern(TIME));
    public static final Parser<String> TEXT = characters(Characters.alphaNumeric.or(is('.'))).map(Object::toString);
    public static final Parser<String> QUOTED_TEXT = characters(not('"')).between(isChar('"'), isChar('"')).map(Object::toString);
    public static final Parser<String> NULL = Parsers.string("null").returns(null);
    public static final Parser<String> TEXT_ONLY = Parsers.or(QUOTED_TEXT, TEXT);
    public static final Parser<String> VALUES = Parsers.or(DATE_AND_TIME, DATE, NULL, TEXT_ONLY);
    public static final Parser<String> NAME = TEXT_ONLY;
    public static final Parser<Void> WILDCARD = isChar('*').ignore();
    public static final Parser<Void> GT = ws('>');
    public static final Parser<Void> GTE = ws(">=");
    public static final Parser<Void> LT = ws('<');
    public static final Parser<Void> LTE = ws("<=");
    public static final Parser<Void> OPERATORS = Parsers.or(GTE, GT, LTE, LT);
    static final Parser<Void> SEPARATORS = ws(':').or(ws('=')).or(OPERATORS.peek());

    public static final Parser<Binary<Predicate<Record>>> OR =
            ws("OR ").map(ignore -> Predicates::or);

    public static final Parser<Binary<Predicate<Record>>> AND =
            Parsers.or(ws("AND"), isChar(' ').many()).map(ignore -> Predicates::and);

    public static final Parser<Pair<String, Callable1<Object, Predicate>>> TEXT_STARTS_WITH = TEXT_ONLY.followedBy(WILDCARD).
            map(valueAndPredicateCreator(value -> startsWith(value.toString())));

    public static final Parser<Pair<String, Callable1<Object, Predicate>>> TEXT_ENDS_WITH = WILDCARD.next(TEXT_ONLY).
            map(valueAndPredicateCreator(value -> endsWith(value.toString())));

    public static final Parser<Pair<String, Callable1<Object, Predicate>>> TEXT_CONTAINS = TEXT_ONLY.between(WILDCARD, WILDCARD).
            map(valueAndPredicateCreator(value -> contains(value.toString())));

    static Parser<Unary<Predicate<Record>>> NEGATION = ws('-').or(ws("NOT")).
            map(ignore -> Predicates::not);

    public static final Parser<Pair<String, Callable1<Object, Predicate>>> DATE_IS = DATE.map(valueAndPredicateCreator(o -> {
        Date dateWithoutTime = (Date) o;
        Date upper = Seconds.add(dateWithoutTime, (24 * 60 * 60) - 1);
        return Predicates.between(dateWithoutTime, upper);
    }));

    public static final Parser<Pair<String, Callable1<Object, Predicate>>> DATE_TIME_IS = DATE_AND_TIME.map(valueAndPredicateCreator(value -> {
        Date dateWithoutMillis = (Date) value;
        Date upper = Dates.add(dateWithoutMillis, MILLISECOND, 999);
        return Predicates.between(dateWithoutMillis, upper);
    }));

    public static final Parser<Pair<String, Callable1<Object, Predicate>>> GREATER_THAN =
            operator(GT, value -> Predicates.greaterThan((Comparable) value));

    public static final Parser<Pair<String, Callable1<Object, Predicate>>> GREATER_THAN_OR_EQUALS =
            operator(GTE, value -> Predicates.greaterThanOrEqualTo((Comparable) value));

    public static final Parser<Pair<String, Callable1<Object, Predicate>>> LESS_THAN_OR_EQUALS =
            operator(LTE, value -> Predicates.lessThanOrEqualTo((Comparable) value));

    public static final Parser<Pair<String, Callable1<Object, Predicate>>> LESS_THAN =
            operator(LT, value -> Predicates.lessThan((Comparable) value));

    public static final Parser<Pair<String, Callable1<Object, Predicate>>> IS_NULL =
            NULL.map(valueAndPredicateCreator(value -> Predicates.nullValue()));

    public static final Parser<Pair<String, Callable1<Object, Predicate>>> TEXT_IS =
            TEXT_ONLY.map(valueAndPredicateCreator(value -> is(value)));

    static Parser<Pair<String, Callable1<Object, Predicate>>> operator(Parser<Void> type, Callable1<Object, Predicate> predicateCreator) {
        return type.next(VALUES).map(valueAndPredicateCreator(predicateCreator));
    }

    static Callable1<String, Pair<String, Callable1<Object, Predicate>>> valueAndPredicateCreator(final Callable1<Object, Predicate> predicateCreator) {
        return value -> Pair.pair(value, predicateCreator);
    }

    public static Parser<Pair<String, Callable1<Object, Predicate>>> VALUE_PREDICATE =
            Parsers.or(GREATER_THAN_OR_EQUALS, LESS_THAN_OR_EQUALS, GREATER_THAN, LESS_THAN, DATE_TIME_IS, DATE_IS,
                    TEXT_CONTAINS, TEXT_STARTS_WITH, TEXT_ENDS_WITH, IS_NULL, TEXT_IS);

    public static Parser<List<Pair<String, Callable1<Object, Predicate>>>> VALUE_PREDICATES = VALUE_PREDICATE.sepBy1(ws(','));
}
