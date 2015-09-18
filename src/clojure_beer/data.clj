(ns clojure-beer.data)

(defn mutual-reviews [reviews first-beer-review second-beer-review]
  "Find mutual criticism for two beer types."
  (let [first-user (data first-beer-review)
        second-user (data second-beer-review)]
    (apply merge {}
           (for [k (keys first-user)
                 :when (contains? second-user k)]
             (assoc {} k [(first-critic k) (second-critic k)])))))