(ns input.csv-dataset
  (:require [incanter.core :as core]
        [incanter.io :as io]
        [somnium.congomongo :as cm]
        [incanter.mongodb :as db]
))

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


;;open connection to db beerdb and insert dataset read from .csv file
(cm/mongo! :db "beerdb")
(cm/mass-insert! :beer-data (:rows data))

;;retrieving inserted dataset
(def beer-data (db/fetch-dataset :beer_data))

;;get column names of dataset
(defn get-column-names []
  (core/col-names beer-data))

(def fetch-data-for-user 
  (cm/fetch :beer_data :where {:review_profileName "stcules"}))

(defn get-reviews-by-username [] 
  (cm/fetch-by-id :by "review_overall"))

(get-reviews-by-username)