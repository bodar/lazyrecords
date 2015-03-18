Provides uniform functional methods to access data in [SQL, Lucene,, SimpleDB, Xml and in-memory](http://code.google.com/p/lazyrecords/source/browse/test/com/googlecode/lazyrecords/RecordsContract.java). Inspired by LINQ. Now you can map/reduce/join across different data sources.

## Quick Start

Choose an implementation:

| **Implementations** | **Usage** | **Transactions** |
|:--------------------|:----------|:-----------------|
| [MemoryRecords](/src/com/googlecode/lazyrecords/memory/MemoryRecords.java) | Test      | No               |
| [STMRecords](/src/com/googlecode/lazyrecords/memory/STMRecords.java) | Test      | Yes              |
| [LuceneRecords](/src/com/googlecode/lazyrecords/lucene/LuceneRecords.java) | Production | No               |
| [SqlRecords](/src/com/googlecode/lazyrecords/sql/SqlRecords.java) | Production | Yes              |
| [SimpleDBRecords](/src/com/googlecode/lazyrecords/simpledb/SimpleDBRecords.java) | Production | No |
| [XmlRecords](/src/com/googlecode/lazyrecords/xml/XmlRecords.java) | Production | No               |


### Create Definitions and Keywords

Definitions are similar to schemas, they tell LazyRecords the name of the records you will store and what fields the records will contain. To access the value of a field you use a keyword;

Keywords are similar to Clojure keywords or Ruby symbols with the addition of type information. The type information is used in the conversion from Java types to the underlying datastore and back.

```java
Keyword<Date> dob = keyword("dob", Date.class);
Keyword<String> name = keyword("name", String.class);
Definition people = definition("people", dob, name);
```

Normally you will make these into constants.

### Create some data

Records supports 3 different modification operations:

| Operation | Description |
|:----------|:------------|
| Add       | Inserts new records into the underlying store (no predicate) |
| Set       | Updates existing records that match the supplied predicate |
| Put       | Inserts or Updates records based on if a predicate matches |

Lets just add some data:

```java
records.add(people,
    record(name, "dan", dob, date(1977, 1, 10)),
    record(name, "matt", dob, date(1975, 1, 10)),
    record(name, "bob", dob, date(1976, 1, 10)));
```

### Query your data

To query our data we `get` our data from records and start using our favourite functional methods like `filter`, `map`, `reduce`

```java
records.get(people); // returns 3 records with all fields
records.get(people).filter(where(name, is("dan"))); // returns 1 record
records.get(people).map(name); // returns "dan", "matt", "bob"
records.get(people).reduce(maximum(dob)); // returns date(1977, 1, 10)
records.get(people).sortBy(ascending(dob)); // returns 3 records sorted by `dob` 
```

Obviously you can chain these together. [More examples...](/test/com/googlecode/lazyrecords/RecordsContract.java)

### Java Support
Version 1.1 requires Java 7 or higher. Version 293 is the last build that supports Java 6.
