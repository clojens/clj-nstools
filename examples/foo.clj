; A namespace used as a template by namespace bar

(ns foo
  (:use [clojure.algo.monads :exclude (cont-m)])
  (:require [clojure.contrib.seq :as su])
  (:import (java.io InputStream)))
