(ns server.clj-db
   (:require [monger.core :as mg]
             [monger.collection :as mc])
   (:import [com.mongodb MongoOptions ServerAddress])
   (:import org.bson.types.ObjectId))

;; localhost, default port
(def connect (mg/connect))

;;connect to tourpediadb
(def db (mg/get-db connect "tourpediadb"))


;;insert data for an admin into db
(defn insert-admin [connect db]
  (mc/insert db "users" { :_id (ObjectId.) :username "John" :password "Doe" :fullname "John Doe" :email "aleksandar.v90@gmail.com"}))

;;insert data for a user into db
(defn insert-user [connect db username password name email]
  (mc/insert-and-return "users" {:username username :password password :name name :email email}))

(insert-admin connect db)
;;disconnect
(let [conn (mg/connect)]
  (mg/disconnect conn))