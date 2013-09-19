# clj-nstools

## Introduction

A typical situation in a Clojure project is that several namespaces
start with the same combination of `use`-`require`-`import` clauses, or at least
have much of these references to the outside world in common. However,
Clojure currently has no way to define a combination of external
references once and then re-use and extend it in individual
namespaces.

## Features

Nstools is an attempt to provide a solution: it permits any namespace to serve
as a template for constructing other namespaces. An enhanced version of the ns
macro, `ns+`, adds the clauses `like`, `clone` and `remove`.

## Example

Suppose you have a namespace `foo` that serves as the template:

```clojure
(ns foo
  (:require (clojure [zip :as zip]
                     [string :as string]
                     [repl :refer [doc source dir]]
                     [walk :as walk])
            [clojure.java.shell :as shell])
  (:import (java.io InputStream)))
```

You can then create a namespace `bar` and make it `like foo`. This
takes over all the references created by the `use`, `require`, and `import`
clauses in `foo`, including aliases defined by `:require ... :as`:

```clojure
(clojure.core/require '[nstools.ns :refer [ns+]])

(ns+ bar
  (:like foo))

(string/reverse "hello world")
```

A `:clone` clause is the same as `:like` followed by `:use` for the same namespace.

A `:remove` clause makes it possible to remove references from a
namespace. It doesn't matter if those references were created by
`:like`, `:clone`, or `:use`. The references need not even exist. It is thus
possible to list in a `:remove` clause all symbols that will be defined
in the namespace later on, guaranteeing that there will be no name
clashes with references to other namespaces, in particular if symbols
are added to later versions of these namespaces.

The `:from` clause provides an alternative cleaner syntax for the
functionality offered by `:use`. The format is `(:from namespace sym1 sym2 ...)`,
which is equivalent to `(:use [namespace :only (sym1 sym2 ...)])`.
It is in fact not fully equivalent, because `:from` allows the specified symbols
to overwrite previously existing references. Another format, to be used with caution,
is `(:from namespace :all)`, which is equivalent to `(:use namespace)`.

## A word on Clojure native clauses

### Example [src: `Clojure official web site`](http://clojure.org/libs)

A simple lib with embedded explanations:

```clojure
(ns com.my-company.clojure.examples.my-utils
  (:import java.util.Date)
  (:use [clojure.contrib.def :only (defvar-)])
  (:require [clojure.contrib.shell-out :as shell]))
```

* The ns form names the lib's namespace and declares its dependencies. Based on its name, this lib must be contained in a Java resource at the classpath-relative path: `com/my_company/clojure/examples/my_utils.clj` (note the translations from period to slash and hyphen to underscore).
* The `:import` clause declares this lib's use of java.util.Date and makes it available to code in this lib using its unqualified name.
* The `:use` clause declares a dependency on the clojure.contrib.def lib for its defvar- function only. defvar- may be used in this lib's code using its unqualified name.
* The `:require` clause declares a dependency on the clojure.contrib.shell-out lib and enables using its members using the shorter namespace alias shell.

### Prefix Lists

It's common for a lib to depend on several other libs whose full names share a common prefix. In calls to require and use (and in :require and :use clauses within an ns form), the common prefix can be extracted and provided once using a prefix list. For example, these two forms are equivalent:

`(require 'clojure.contrib.def 'clojure.contrib.except 'clojure.contrib.sql)`
`(require '(clojure.contrib def except sql))`


## Disclaimer

The majority of this document was left untouched by me, the one who cloned this
project, not the author. By now, personally, I try not to use `(:use ...)` anymore.
It will, in time, probably become deprecated and already it is getting out of use in
the wild (mostly older projects still have lots of use clauses).

Please keep those things in mind as mentioned here, I cloned this for my own (dev/test)
benefit, by no means would I suggest you use something like this in production.

**Use at your own risk!**


