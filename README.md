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

## Usage

Suppose you have a namespace `foo` that serves as the template:

```clojure
(ns foo
  (:refer-clojure :exclude [read read-string alter commute ref-set ensure])
  (:require (clojure [zip :as zip]
                     [repl :refer [doc source dir]]
                     [walk :as walk]
                     [edn :refer [read read-string]]
                     [pprint :refer [pprint print-table cl-format]])
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

; Use the alias `shell`
(shell/sh "echo" "Hello world")
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

## Disclaimer

The majority of this document was left untouched by me, the one who cloned this
project, not the author. By now, personally, I try not to use `(:use ...)` anymore.
It will, in time, probably become deprecated and already it is getting out of use in
the wild (mostly older projects still have lots of use clauses).

Please keep those things in mind as mentioned here, I cloned this for my own (dev/test)
benefit, by no means would I suggest you use something like this in production.

**Use at your own risk!**


