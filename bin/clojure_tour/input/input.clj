(ns clojure-tour.input
    (:use clojure-csv.core))

(defn find-results
 [stream]
 (for [record stream
       :while (seq (first record))]
   record))

;;read csv data from file
(def rdr (clojure.java.io/reader "amsterdam-restaurant.csv"))
(def csv (parse-csv rdr))

(def results (doall (find-results csv)))

(.close rdr)
(parse-csv rdr)