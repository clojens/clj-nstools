;; Enhanced namespace management

;; by Konrad Hinsen  (based on the ns macro by Rich Hickey)
;; last updated March 5, 2010

;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0
;; (http://opensource.org/licenses/eclipse-1.0.php) which can be found
;; in the file epl-v10.html at the root of this distribution. By using
;; this software in any fashion, you are agreeing to be bound by the
;; terms of this license. You must not remove this notice, or any
;; other, from this software.

(ns nstools.ns
  "An improved version of Clojure's ns macro"
  {:author "Konrad Hinsen"})

(defn like
  "Take over all refers, aliases, and imports from the namespace named by
   ns-sym into the current namespace. Use :like in the ns+ macro in preference
   to calling this directly."
  [ns-sym]
  (require ns-sym)
  (doseq [[alias-sym other-ns] (ns-aliases ns-sym)]
    (alias alias-sym (ns-name other-ns)))
  (doseq [[sym ref] (ns-refers ns-sym)]
    (ns-unmap *ns* sym)
    (. *ns* (refer sym ref))
  (doseq [[sym ref] (ns-imports ns-sym)]
    (. *ns* (importClass ref)))))

(defn clone
  "Take over all refers, aliases, and imports from the namespace named by
   ns-sym into the current namespace, and add refers to all vars interned.
   Use :clone in the ns+ macro in preference to calling this directly."
  [ns-sym]
  (like ns-sym)
  (use ns-sym))

(defn from
  "Add refers to syms from ns-sym to the current namespace, replacing
   existing refers if necessary. If :all is given instead of syms,
   all symbols from ns-sym are referred to, making this equivalent
   to (use ns-sym). Use :from in the ns+ macro in preference to calling
   this directly."
  [ns-sym & syms]
  (if (= syms '(:all))
    (use ns-sym)
    (do
      (require ns-sym)
      (doseq [sym syms]
	(ns-unmap *ns* sym)
	(. *ns* (refer sym (ns-resolve ns-sym sym)))))))

(defn remove-from-ns
  "Remove symbols from the namespace. Use :remove in the ns+ macro in
   preference to calling this directly."
  [& syms]
  (doseq [sym syms]
    (ns-unmap *ns* sym)))

(def #^{:private true} reference-map
  {:clone 'nstools.ns/clone
   :from 'nstools.ns/from
   :gen-class 'clojure.core/gen-class
   :import 'clojure.core/import
   :like 'nstools.ns/like
   :load 'clojure.core/load
   :refer 'clojure.core/refer
   :refer-clojure 'clojure.core/refer-clojure
   :remove 'nstools.ns/remove-from-ns
   :require 'clojure.core/require
   :use 'clojure.core/use})

;
; The ns+ macro is not very different from clojure.core/ns. The only
; functional difference is that in the presence of a :like or :clone clause it
; doesn't generate the default :refer-clojure clause. The other modifications
; are required only to make it work with the function "like" above that is
; not defined in clojure.core.
;
; The name had to be changed to ns+ because "ns" and "in-ns" cannot be
; redefined in any namespace.
;
(defmacro ns+
  "Sets *ns* to the namespace named by name (unevaluated), creating it
  if needed.  references can be zero or more of: (:refer-clojure ...)
  (:like ...) (:require ...) (:use ...) (:import ...) (:load ...) (:gen-class)
  with the syntax of refer-clojure/like/require/use/import/load/gen-class
  respectively, except the arguments are unevaluated and need not be
  quoted. (:gen-class ...), when supplied, defaults to :name
  corresponding to the ns name, :main true, :impl-ns same as ns, and
  :init-impl-ns true. All options of gen-class are
  supported. The :gen-class directive is ignored when not
  compiling. If :gen-class is not supplied, when compiled only an
  nsname__init.class will be generated. If :refer-clojure is not used, a
  default (refer 'clojure) is used.  Use of ns is preferred to
  individual calls to in-ns/require/use/import:

  (ns+ foo.bar
    (:refer-clojure :exclude [ancestors printf])
    (:require (clojure.contrib sql sql.tests))
    (:use (my.lib this that))
    (:import (java.util Date Timer Random)
             (java.sql Connection Statement)))"
  {:arglists '([name docstring? attr-map? references*])}
  [name & references]
  (let [process-reference
        (fn [[kname & args]]
          `(~(reference-map kname)
             ~@(map #(list 'quote %) args)))
        docstring  (when (string? (first references)) (first references))
        references (if docstring (next references) references)
        name (if docstring
               (vary-meta name assoc :doc docstring)
               name)
        metadata   (when (map? (first references)) (first references))
        references (if metadata (next references) references)
        name (if metadata
               (vary-meta name merge metadata)
               name)
        gen-class-clause (first (filter #(= :gen-class (first %)) references))
        gen-class-call
          (when gen-class-clause
            (list* `gen-class :name (.replace (str name) \- \_) :impl-ns name :main true (next gen-class-clause)))
        references (remove #(= :gen-class (first %)) references)
        ]
    `(do
       (clojure.core/in-ns '~name)
       (with-loading-context
        ~@(when gen-class-call (list gen-class-call))
        ~@(when (and (not= name 'clojure.core)
		     (not-any? (comp #{:refer-clojure :like :clone} first) references))
            `((clojure.core/refer '~'clojure.core)))
        ~@(map process-reference references)))))
