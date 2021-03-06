package com.googlecode.lazyrecords.xml;

import com.googlecode.lazyrecords.AliasedKeyword;
import com.googlecode.lazyrecords.ToRecord;
import com.googlecode.totallylazy.functions.Function2;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.xml.Xml;
import com.googlecode.lazyrecords.Keyword;
import com.googlecode.lazyrecords.Record;
import com.googlecode.lazyrecords.SourceRecord;
import com.googlecode.lazyrecords.xml.mappings.XmlMappings;
import org.w3c.dom.Node;

import java.util.Iterator;

import static com.googlecode.lazyrecords.Keyword.constructors.keyword;

public class XmlSequence extends Sequence<Record> {
    private final Sequence<Node> nodes;
    private final XmlMappings mappings;
    private final Sequence<Keyword<?>> definitions;

    public XmlSequence(Sequence<Node> nodes, XmlMappings mappings, Sequence<Keyword<?>> definitions) {
        this.nodes = nodes;
        this.mappings = mappings;
        this.definitions = definitions;
    }

    public Iterator<Record> iterator() {
        return nodes.map(asRecord()).iterator();
    }

    private ToRecord<Node> asRecord() {
        return new ToRecord<Node>() {
            public Record call(final Node node) throws Exception {
                return definitions.fold(SourceRecord.record(node), populateFrom(node));
            }
        };
    }

    private Function2<Record, Keyword<?>, Record> populateFrom(final Node node) {
        return (nodeRecord, keyword) -> {
            Sequence<Node> nodes1 = Xml.selectNodes(node, xpath(keyword));
            if (nodes1.isEmpty()) {
                return nodeRecord;
            }
            Object value = mappings.get(keyword.forClass()).from(nodes1);
            return nodeRecord.set(keyword(keyword), value);
        };
    }

    private String xpath(Keyword<?> keyword) {
        if(keyword instanceof AliasedKeyword){
            return ((AliasedKeyword) keyword).source().name();
        }
        return keyword.name();
    }
}
