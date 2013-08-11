(ns nstools.generic-math
  "A namespace that uses the generic operations from
   clojure.contrib.generic instead of the number-only arithmetic
   and comparison functions from clojure.core. It also includes all
   of clojure.contrib.generic.math-functions."
  (:refer-clojure :exclude (+ - * / zero? pos? neg? > >= < <= min max))
  (:use [clojure.contrib.generic.arithmetic
	 :only (+ - * /)]
	[clojure.contrib.generic.comparison
	 :only (zero? pos? neg? > >= < <= min max)]
	[clojure.contrib.generic.math-functions]))
