package com.googlecode.lazyrecords.parser;

import com.googlecode.lazyrecords.Keyword;
import com.googlecode.lazyrecords.Record;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.template.Template;
import com.googlecode.totallylazy.template.Templates;

import java.util.Date;

import static com.googlecode.totallylazy.Predicates.instanceOf;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.time.DateConverter.functions.format;

public class ParametrizedParser implements PredicateParser {
    private final PredicateParser parser;
    private final ParserDateConverter dateConverter;
    private final ParserFunctions parserFunctions;
    private final ParserParameters data;

    public ParametrizedParser(PredicateParser parser, ParserDateConverter dateConverter, ParserParameters data) {
        this(parser, dateConverter, new ParserFunctions(), data);
    }

    public ParametrizedParser(PredicateParser parser, ParserDateConverter dateConverter, ParserFunctions parserFunctions, ParserParameters data) {
        this.parser = parser;
        this.dateConverter = dateConverter;
        this.parserFunctions = parserFunctions;
        this.data = data;
    }

    public Predicate<Record> parse(String query, Sequence<? extends Keyword<?>> implicits) throws IllegalArgumentException {
        try {
            Templates templates = Templates.defaultTemplates();
            templates.add(instanceOf(Date.class), format(dateConverter));
            parserFunctions.addTo(templates);

            Template template = Template.template(query, templates);
            String newQuery = template.render(data.values());
            return parser.parse(newQuery, implicits);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
