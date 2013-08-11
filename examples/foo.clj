; A namespace used as a template by namespace bar

(ns foo
  (:use [clojure.contrib.monads :exclude (cont-m)])
  (:require [clojure.contrib.seq :as su])
  (:import (java.io InputStream)))
