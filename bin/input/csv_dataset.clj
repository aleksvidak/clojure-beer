(ns input.csv-dataset
  (:require [incanter.core :as core]
        [incanter.io :as io]
        [somnium.congomongo :as cm]
        [incanter.mongodb :as db]))

;;read dataset from .csv; delimiter is comma
(def data
  (read-dataset
    "xaa.csv"
     :header true :delim \,))

;;view cols names
(col-names data)

;;view number of rows & cols
(dim data)
(view data)

;;open connection to db beerdb 
(cm/mongo! :db "beerdb")

;;insert dataset read from .csv file
(cm/mass-insert! :beer-data (:rows data))

;;retrieving inserted dataset
(def beer-data (db/fetch-dataset :beer_data))

;;get column names of dataset
(defn get-column-names []
  "Get column names for existing dataset."
  (core/col-names beer-data))

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



;;get reviews by user for cosine similarity
(defn get-reviews-by-user-cs [username beer]
  "Get user review for specified beer."
  (flatten (map vals
                (cm/fetch :beer_data :only
                          {:_id false 
                           :review_aroma true
                                         :review_palate true
                                         :review_taste true
                                         :review_appearance true} 
                          :where 
                          {:review_profileName username
                           :beer_name beer}))))

;;get distinct users
(def allusers 
  (distinct (flatten (map vals 
                          (cm/fetch 
                            :beer_data :only
                            {:_id false
                             :review_profileName true})))))

;;get cleaned data
;;work to do!!!!!!!!!!!!
(defn get-all-reviews []
  (for [u allusers] (apply hash-map 
         (flatten (map vals 
                       (cm/fetch :beer_data :only
                                 {:_id false 
                                  :beer_name true 
                                  :review_overall true} 
                                 :where 
                                 {:review_profileName u}))))))
