(ns database.clj-dbbeers
  (:require [incanter.core :as core]
        [incanter.io :as io]
        [somnium.congomongo :as cm]
        [incanter.mongodb :as db]
        [ring.util.response :as resp]))

;;get beers from beer_data collection
(defn get-beers []
  (distinct (flatten (map vals 
                          (cm/fetch 
                            :beers :only
                            {:_id false
                             :beer_name true})))))

;;check if beer exists
(defn beer-exists? [beer]
  "Check if beer with supplied name exists in the database."
  (if (not= 0 (count 
                (filter not-empty 
                        (cm/fetch :beers :only {:_id false} :where {:beer_name beer})))) true false))

;;insert beer in beers collection
(defn insert-beer [beer]
  "Insert new beer under condition that there isn't another one with same name."
  (if-not (beer-exists? beer)
    (do 
    (cm/insert! :beers {:beer_name beer})
    (resp/response "Beer added to the database!")
    (resp/response "Beer is already in the database!"))
    (resp/response "Beer exists!")))

;;delete beer
(defn delete-beer [beer]
  "Delete beer with supplied name."
  (do 
    (cm/destroy! :beers {:beer_name beer})
    (cm/destroy! :beer_data {:beer_name beer})
    (resp/response "Beer deleted!")))

;;retrieving inserted dataset
(defn beer-data []
  (db/fetch-dataset :beer_data))

;;get column names of dataset
(defn get-column-names []
  "Get column names for existing dataset."
  (core/col-names (beer-data)))

;;get full data for supplied username
(defn fetch-data-for-user [username]
  "Get raw origial data from dataset for specified user."
  (cm/fetch :beer_data :where 
            {:review_profileName username}))

;;get reviews for supplied beer name
(defn fetch-data-for-beer [beername]
  "Get all reviews for specified beer."
  (cm/fetch :beer_data :only 
            {:_id false 
             :review_profileName true 
             :beer_beerId true 
             :beer_name true 
             :review_overall true} 
            :where 
            {:beer_name beername}))

;;get reviews by user for euclidean distance
(defn get-reviews-by-user [username]
  "Get all reviews for a specified user."
  (apply hash-map 
         (flatten (map vals 
                       (cm/fetch :beer_data :only
                                 {:_id false 
                                  :beer_name true 
                                  :review_overall true} 
                                 :where 
                                 {:review_profileName username})))))


;;get distinct users from beer_data collection
(defn get-reviewers [] 
  (distinct (flatten (map vals 
                          (cm/fetch 
                            :beer_data :only
                            {:_id false
                             :review_profileName true})))))



;;get cleaned data for n users
;;transformation needed
(defn get-n-user-reviews [n]
  "Return n user reviews (beer & score pairs for n users)."
  (for [u (take n (get-reviewers))] 
    (hash-map u 
      (apply hash-map 
         (flatten (map vals 
                       (cm/fetch :beer_data :only
                                 {:_id false 
                                  :beer_name true 
                                  :review_overall true} 
                                 :where 
                                 {:review_profileName u})))))))

;;insert new review
(defn insert-new-review [username beer score]
  "Insert new review for beer with score by given user."
  (do 
    (cm/insert! :beer_data {:review_profileName username :beer_name beer :review_overall score})
    (resp/response "Review added!")))

(defn all-users []
  (distinct (flatten (map vals 
                          (cm/fetch 
                            :beer_data :only
                            {:_id false
                             :review_profileName true})))))

(defn get-all-reviews [n]
  "Return n user reviews (beer & score pairs for n users)."
  (for [u (take n (all-users))] 
    (hash-map u 
      (apply hash-map 
          (flatten (map vals 
                        (cm/fetch :beer_data :only
                                  {:_id false 
                                   :beer_name true 
                                   :review_overall true} 
                                  :where 
                                 {:review_profileName u})))))))

