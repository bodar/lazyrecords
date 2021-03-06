package com.googlecode.lazyrecords.sql.expressions;

import com.googlecode.totallylazy.Option;

public class AnsiDerivedColumn extends CompoundExpression implements DerivedColumn {
    private final ValueExpression valueExpression;
    private final Option<AsClause> asClause;
    private final Class<?> aClass;

    private AnsiDerivedColumn(ValueExpression valueExpression, Option<AsClause> asClause, final Class<?> aClass) {
        super(valueExpression, Expressions.expression(asClause));
        this.valueExpression = valueExpression;
        this.asClause = asClause;
        this.aClass = aClass;
    }

    public static AnsiDerivedColumn derivedColumn(ValueExpression valueExpression, Option<AsClause> asClause, final Class<?> aClass) {
        return new AnsiDerivedColumn(valueExpression, asClause, aClass);
    }

    @Override
    public ValueExpression valueExpression() {
        return valueExpression;
    }

    @Override
    public Option<AsClause> asClause() {
        return asClause;
    }

    @Override
    public Class<?> forClass() {
        return aClass;
    }
}
