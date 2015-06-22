package com.googlecode.lazyrecords.sql.grammars;

import com.googlecode.lazyrecords.Aggregate;
import com.googlecode.lazyrecords.AliasedKeyword;
import com.googlecode.lazyrecords.CompositeKeyword;
import com.googlecode.lazyrecords.Definition;
import com.googlecode.lazyrecords.Join;
import com.googlecode.lazyrecords.Joiner;
import com.googlecode.lazyrecords.Keyword;
import com.googlecode.lazyrecords.Record;
import com.googlecode.lazyrecords.RecordTo;
import com.googlecode.lazyrecords.sql.expressions.*;
import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;

import java.util.Comparator;

public interface SqlGrammar {
    SelectExpression selectExpression(Option<SetQuantifier> setQuantifier,
                                      Sequence<? extends Keyword<?>> selectList,
                                      Definition fromClause,
                                      Option<Predicate<? super Record>> whereClause,
                                      Option<Comparator<? super Record>> orderByClause,
                                      Option<Sequence<? extends Keyword<?>>> groupByClause,
                                      Option<Integer> offsetClause,
                                      Option<Integer> fetchClause);

    SelectList selectList(Sequence<? extends Keyword<?>> select);

    FromClause fromClause(Definition definition);

    WhereClause whereClause(Predicate<? super Record> where);

    OrderByClause orderByClause(Comparator<? super Record> orderBy);

    OffsetClause offsetClause(int number);

    FetchClause fetchClause(int number);

    GroupByClause groupByClause(Sequence<? extends Keyword<?>> columns);

    DerivedColumn derivedColumn(Function1<? super Record, ?> callable);

    ValueExpression valueExpression(Function1<? super Record, ?> callable);

    ValueExpression concat(Sequence<? extends Keyword<?>> keywords);



    Expression insertStatement(Definition definition, Record record);

    Expression updateStatement(Definition definition, Predicate<? super Record> predicate, Record record);

    Expression deleteStatement(Definition definition, Option<? extends Predicate<? super Record>> predicate);

    Expression createTable(Definition definition);

    Expression dropTable(Definition definition);

    AsClause asClause(String alias);

    ValueExpression valueExpression(Keyword<?> keyword);

    ValueExpression valueExpression(AliasedKeyword aliasedKeyword);

    ValueExpression valueExpression(Aggregate aggregate);

    ValueExpression valueExpression(CompositeKeyword<?> composite);

    JoinSpecification joinSpecification(Joiner joiner);

    JoinType joinType(Join join);

    ExpressionBuilder join(ExpressionBuilder primary, ExpressionBuilder secondary, JoinType type, JoinSpecification specification);

    ExpressionBuilder join(ExpressionBuilder builder, Join join);


    class functions {
        public static Function1<Predicate<? super Record>, WhereClause> whereClause(final SqlGrammar grammar) {
            return grammar::whereClause;
        }

        public static RecordTo<Expression> insertStatement(final SqlGrammar grammar, final Definition definition) {
            return new RecordTo<Expression>() {
                public Expression call(Record record) throws Exception {
                    return grammar.insertStatement(definition, record);
                }
            };
        }

        public static Function1<Pair<? extends Predicate<? super Record>, Record>, Expression> updateStatement(final SqlGrammar grammar, final Definition definition) {
            return recordPair -> grammar.updateStatement(definition, recordPair.first(), recordPair.second());
        }

        public static Function1<Keyword<?>, DerivedColumn> derivedColumn(final SqlGrammar grammar) {
            return grammar::derivedColumn;
        }

        public static Function1<? super Comparator<? super Record>,OrderByClause> orderByClause(final SqlGrammar grammar) {
            return grammar::orderByClause;
        }

        public static Function1<? super Sequence<? extends Keyword<?>>,GroupByClause> groupByClause(final SqlGrammar grammar) {
            return grammar::groupByClause;
        }
    }
}
