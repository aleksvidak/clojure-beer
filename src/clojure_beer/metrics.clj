(ns clojure-beer.metrics
  (:use [database.clj-dbbeers :only [get-reviews-by-user get-all-reviews]]))


;;count euclidean-distance between two users, taken into account 
;;overall marks for beers that both users reviewed
;;d(p,q)=n∑i=√1/(pi−qi)2−−−−−−−−−−
;;
(defn euclidean-distance [person1 person2]
  "Get euclidean distance for two specified persons (usernames)."
  (let [shared-items (filter person1 (keys person2))
        score (reduce (fn [score beer]
                        (let [score1 (person1 beer)
                              score2 (person2 beer)]
                          (+ score (Math/pow (- score1 score2) 2))))
                      0 shared-items)]
    (if (= (count shared-items) 0)
      0
      (/ 1 (+ 1 score)))))


;;test
;;(euclidean-distance (csv/get-reviews-by-user "johnmichaelsen") (csv/get-reviews-by-user "stcules"))

;;anonymous function with two params, the second is destructured
(defn similar-users [person]
  (let [person-score (database.clj-dbbeers/get-reviews-by-user person)
        data (into {} (database.clj-dbbeers/get-all-reviews 20))]
  (reduce (fn[k v]
    (let [name (first v)
          score (second v)
          similarity (euclidean-distance person-score score)]
              (assoc k name similarity) )) {} data)))

;;return similar users without zero similarity to one supplied, from limited dataset
(defn sort-filter-similar-users [person]
  (filter 
   #(not= 0 (second %))
   (reverse (sort-by second (similar-users person)))))

;;test
;;(sort-filter-similar-users "phishisphunk")

;;beer recommendations

(defn weighted-scores [similarity person]
  (let [data (into {} (get-all-reviews 20))]
    (reduce 
     (fn [k v]
       (let [other (first v) score (second v)
             diff (filter #(not (contains? (data person) (key %))) (data other))
             weighted-score (apply hash-map
                                  (interleave (keys diff) 
                                              (map #(* % score) (vals diff))))]
         (assoc k other weighted-score))) {} similarity)))

;;test
;;(weighted-scores (sort-filter-similar-users "phishisphunk") "phishisphunk")

(defn totals [data]
  (reduce (fn [k v] (merge-with #(+ %1 %2) k v)) {} (vals data)))

;;(totals (weighted-scores (sort-filter-similar-users "phishisphunk") "phishisphunk"))


(defn sum-sims [weighted-scores scores similar-users]
  (reduce (fn [k v]
            (let [beer (first v)
                  rated-users (reduce 
                               (fn [k v] (if (contains? (val v) beer) 
                                          (conj k (key v)) k)) 
                               [] weighted-scores)
                  similarities (apply + (map #(similar-users %) rated-users))]
              (assoc k beer similarities) ) ) {} scores))

(defn recommend [person]
  (let [similar-users (into {} (sort-filter-similar-users person))
        weighted-scores (weighted-scores similar-users  person)
        scores (totals weighted-scores)
        sims (sum-sims weighted-scores scores similar-users)]
    (if (= scores {})
      (clojure.lang.PersistentHashMap/EMPTY)
      (apply assoc {} (interleave (keys scores) (map #(/ (second %) (sims (first %))) scores))))))

;;(take 10 (recommend "phishisphunk"))
