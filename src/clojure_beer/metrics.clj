(ns clojure-beer.metrics
  (:use [input.csv-dataset :as csv]))

(defn denominator 
  "Calculate denominator for cosine similarity formula."
  [first-person second-person]
  (let [v1 (map * first-person first-person)
        v2 (map * second-person second-person)]
  (* (java.lang.Math/sqrt (reduce + v1)) (java.lang.Math/sqrt (reduce + v2)))))

(defn numerator
  "Calculate numerator for cosine similarity formula."
  [first-person second-person]
    (reduce + (map * first-person second-person)))

(defn cosine-similarity
  "Get cosine similarity from two reviews."
  [first-person second-person]
  (/ (numerator first-person second-person) (denominator first-person second-person)))

;;test
;;(cosine-similarity [0.15 0.15 0.2 0.25] [0.4 0.45 0.45 0.4])


;;count euclidean-distance between two users, taken into account 
;;overall marks for beers that both users reviewed
;;d(p,q)=∑i=1n(pi−qi)2−−−−−−−−−−√
;;
(defn euclidean-distance [person1 person2]
  "Get euclidean distance for two specified persons (usernames)."
  (let [shared-items (filter person1 (keys person2))
        mark (reduce (fn [mark beer]
                        (let [mark1 (person1 beer)
                              mark2 (person2 beer)]
                          (+ mark (Math/pow (- mark1 mark2) 2))))
                      0 shared-items)]
    (if (= (count shared-items) 0)
      0
      (/ 1 (+ 1 mark)))))


;;test
;;(euclidean-distance (csv/get-reviews-by-user "MadeInOregon") (csv/get-reviews-by-user "rawthar"))


