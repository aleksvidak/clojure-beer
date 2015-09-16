(ns server.clj-db
   (:require [monger.core :as mg]
             [monger.collection :as mc])
   (:import org.bson.types.ObjectId))

;; localhost, default port
(def connect (mg/connect))

;;connect to tourpediadb
(def db (mg/get-db connect "beerdb"))


;;insert data for an admin into db
(defn insert-admin [connect db]
  (mc/insert db "users" { :_id (ObjectId.) :username "John" :password "Doe" :fullname "John Doe" :email "aleksandar.v90@gmail.com"}))

;;insert data for a user into db
(defn insert-user [connect db username password fullname email]
  (mc/insert-and-return db "users" {:username username :password password :fullname fullname :email email}))


;;disconnect
(defn disconnect [connect]
  (mg/disconnect connect))

(insert-admin connect db)