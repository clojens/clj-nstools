; Make a namespace with the same refers, imports, and aliases as foo,
; then remove + and - (from clojure.core)

(clojure.core/use 'nstools.ns)
(ns+ bar
  (:clone foo)
  (:remove + -))

(prn (su/flatten [[1 2] [3 4]]))

; the following line should crash
(prn (+ 2 3))
