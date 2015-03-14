### Goals ###

One of the goals of LR is to support a uniform interface (functional collection methods and functions) to data access. LR will never do ORM as I strongly believe this is an anti pattern, data should be free and I should be able to join data from an XML source to an Java Object graph to a SQL table.

Clojure/Rich Hickey talks about [this exact problem](http://clojure.org/datatypes#toc4) but unfortunately the [SQL clojure library](http://clojure.github.com/clojure-contrib/sql-api.html) seems to want to bring SQL language to clojure instead of the functional world to SQL.

LR was inspired by both clojure and LINQ for .NET but tries to stay purely in the functional / collections world.