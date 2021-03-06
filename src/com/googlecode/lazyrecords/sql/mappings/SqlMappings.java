package com.googlecode.lazyrecords.sql.mappings;

import com.googlecode.lazyrecords.mappings.StringMappings;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Unchecked;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import static com.googlecode.totallylazy.Maps.map;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.numbers.Numbers.not;
import static com.googlecode.totallylazy.numbers.Numbers.numbers;
import static com.googlecode.totallylazy.numbers.Numbers.range;
import static com.googlecode.totallylazy.numbers.Numbers.sum;

public class SqlMappings {
    private final Map<Class, SqlMapping<Object>> map = map();
    private final StringMappings stringMappings;

    public SqlMappings(StringMappings stringMappings) {
        this.stringMappings = stringMappings;
        add(Boolean.class, new BooleanMapping());
        add(Date.class, new DateMapping());
        add(Timestamp.class, new TimestampMapping());
        add(Integer.class, new IntegerMapping());
        add(int.class, new IntegerMapping());
        add(BigDecimal.class, new BigDecimalMapping());
        add(Number.class, new BigDecimalMapping());
        add(Long.class, new LongMapping());
        add(long.class, new LongMapping());
    }

    public SqlMappings() {
        this(new StringMappings());
    }

    public <T> SqlMappings add(final Class<T> type, final SqlMapping<? extends T> mapping) {
        map.put(type, Unchecked.<SqlMapping<Object>>cast(mapping));
        return this;
    }

    public <T> SqlMappings add(final Class<T> type, final com.googlecode.lazyrecords.mappings.StringMapping<T> mapping) {
        stringMappings.add(type, mapping);
        return this;
    }

    public SqlMapping<Object> get(final Class aClass) {
        if (!map.containsKey(aClass)) {
            return new ObjectMapping(aClass, stringMappings);
        }
        return map.get(aClass);
    }

    public Object getValue(final ResultSet resultSet, Integer index, final Class aClass) throws SQLException {
        return get(aClass).getValue(resultSet, index);
    }

    public Object getValue(final CallableStatement statement, Integer index, final Class aClass) throws SQLException {
        return get(aClass).getValue(statement, index);
    }

    public void addValues(PreparedStatement statement, Sequence<Object> values) throws SQLException {
        for (Pair<Integer, Object> pair : range(1).safeCast(Integer.class).zip(values)) {
            Integer index = pair.first();
            Object value = pair.second();
            get(value == null ? Object.class : value.getClass()).setValue(statement, index, value);
        }
    }

}