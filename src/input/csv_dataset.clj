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

;;view inserted data
(view (db/fetch-dataset :beer-data))
