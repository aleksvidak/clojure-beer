(ns input.json-dataset
  (:require [clojure.data.json :as json]))


(json/read-str json.txt)

(with-open [reader (clojure.java.io/reader "http://tour-pedia.org/api/getReviews?location=Rome&language=en&category=poi")]
  (json/read reader))

(with-open [reader (clojure.java.io/reader "foo.json")]
  (json/read reader))

(with-open [writer (clojure.java.io/writer "attraction.json")]
  (json/write [(with-open [reader (clojure.java.io/reader "http://tour-pedia.org/api/getReviews?location=Barcelona&language=en")]
  (json/read reader))] writer))
