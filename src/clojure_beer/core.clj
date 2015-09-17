(ns clojure-beer.core)


(defn denominator 
  "Calculate denominator for cosine similarity formula."
  [first-vector second-vector]
  (let [v1 (map * first-vector first-vector)
        v2 (map * second-vector second-vector)]
  (* (java.lang.Math/sqrt (reduce + v1)) (java.lang.Math/sqrt (reduce + v2)))))

(defn numerator
  "Calculate numerator for cosine similarity formula."
  [first-vector second-vector]
    (reduce + (map * first-vector second-vector)))

(defn cosine-similarity
  "Get cosine similarity from two reviews."
  [first-vector second-vector]
  (/ (numerator first-vector second-vector) (denominator first-vector second-vector)))

;;test
;;(cosine-similarity [1 2 3] [4 3 6])




