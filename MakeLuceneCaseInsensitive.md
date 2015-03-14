# Rationale #

You have users that want to find things but they don't know (or care) about the case of the search term.


# Technical Background #

By default, LazyRecords respects the case of your predicate values. The value Lucene uses for searching (the _indexed_ value) is different from the one which it returns to LazyRecord.

To make Lucene go case-insensitive, we need to specify that Lucene should index every value in one case, and then convert our predicates' search terms into that same case.


# How To #
There are two different things you need to use:
  * A case-insensitive LuceneStorage
  * A case-insensitive LuceneQueryPreprocessor to wire into LuceneRecords.
You can find an implementation of both of these in [com.googlecode.lazyrecords.lucene.CaseInsensitive](https://code.google.com/p/lazyrecords/source/browse/src/com/googlecode/lazyrecords/lucene/CaseInsensitive.java).

**Note that you can't switch analyzers for an existing index -- see below for more details**

If you are using a LucenePartitionedIndex, CaseInsensitive provides a Function2 to activate a LuceneStorage for you.

# Reusing an existing index #
The analyser you use to index content has to be compatible with the Predicates you produce from LazyRecords. This effectively means you need to reindex your content if you want to switch to a case-insensitive index.

An alternative to re-indexing all of your source data from scratch is to use the [IndexAnalyzerMigrator](https://code.google.com/p/lazyrecords/source/browse/src/com/googlecode/lazyrecords/lucene/IndexAnalyzerMigrator.java) to create a copy of an index with a different analyzer. This is the recommended approach if re-indexing would take a long time.