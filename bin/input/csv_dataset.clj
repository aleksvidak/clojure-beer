(ns input.csv-dataset
  (:require [incanter.core :as core]
        [incanter.io :as io]
        [somnium.congomongo :as cm]
        [database.clj-dbbeers :only [insert-beer get-beers]]))

;;read dataset from .csv; delimiter is comma
(def data
  (io/read-dataset
   "xaa.csv"
   :header true :delim \,))

;;view cols names
;;(core/col-names data)

;;view number of rows & cols
;;(core/dim data)

;;define connection to db beerdb 
(def connection
  (cm/make-connection "beerdb"
                     :host "127.0.0.1"
                     :port 27017))

;;set connection globally
(cm/set-connection! connection)
(cm/set-write-concern connection :strict)

;;insert beers from beer_data collection to beers collection
(defn insert-beers-from-collection []
  (for [beer (database.clj-dbbeers/get-beers)]
    (database.clj-dbbeers/insert-beer beer)))

;;initial admin username "admin", password "pass"
(defn insert-admin []
  "Initially insert user with admin role to be able to use the application."
  (cm/insert! :users 
              {:username "admin"
                            :password (creds/hash-bcrypt "pass")
                            :roles #{"admin"}}))

;;initial insertion of dataset read from .csv file
;;create beer collection
(defn initialization []
  (do
    (dorun (cm/mass-insert! :beer_data (:rows data)))
    (cm/create-collection! :beers)
    (insert-beers-from-collection)
    (insert-admin)
    (println "Initialization done!")))


(defn -main [& args]
  (initialization))


