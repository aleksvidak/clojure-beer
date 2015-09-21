(ns server.clj-db
   (:require [somnium.congomongo :as cm]))

;;define connection to db beerdb 
(def connection
  (cm/make-connection "beerdb"
                     :host "127.0.0.1"
                     :port 27017))

;;set connection globally
(cm/set-connection! connection)

(defn insert-admin []
  (cm/insert! :users
           {:fullname "John Doe" :username "John" :password "Doe" :email "aleksandar.v90@gmail.com" :role "admin"}))

;;check if user exists in the db
(defn user-exists? [username]
  (if (= 1 (cm/fetch-count :users :where {:username username})) true false))
  
;;insert data for the user into db - collection users
;;initial admin username "John", password "Doe"
(defn insert-user [fullname username password email role]
  "Insert new user under condition that there isn't another one with same username."
  (if-not (user-exists? username)
    (cm/insert! :users
           {:fullname fullname :username username :password password :email email :role role})))

;;get all admins in users collection 
(defn get-all-admins []
  (cm/fetch :users :where
            {:role "admin"}))

;;get all users in users collection 
(defn get-all-users []
  (cm/fetch :users :where
            {:role "user"}))

;;get user by supplied username
(defn get-user [username]
  (cm/fetch :users :where
            {:username username}))

;;delete user with supplied username
(defn delete-user [username]
  "Delete user with supplied username."
  (cm/destroy! :users {:username username}))


;;disconnect
(defn disconnect [conn]
  "Disconnect from database."
  (cm/close-connection conn))



