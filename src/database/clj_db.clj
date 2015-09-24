(ns server.clj-db
   (:require [somnium.congomongo :as cm]
             [cemerick.friend.credentials :as creds]))

;;define connection to db beerdb 
(def connection
  (cm/make-connection "beerdb"
                     :host "127.0.0.1"
                     :port 27017))

;;set connection globally
(cm/set-connection! connection)
(cm/set-write-concern connection :strict)


;;change keywords to strings, because of cemerick.friend required formated data
(defn string-keys [m]
  (into (empty m) (for [[k v] m] [(name k) v])))

;;fetch users from database
;;not working!!!!!!!!
(def users (atom 
             (string-keys 
               (for [x (keys (into {} (cm/fetch :users :only {:_id false})))]
                 (assoc-in (into {} 
                               (cm/fetch :users :only {:_id false})) [x :roles] #{::user})))))

(derive ::admin ::user)

;;initial admin username "admin", password "pass"
(defn insert-admin []
  (cm/insert! :users 
              {"admin" {:username "admin"
                            :password (creds/hash-bcrypt "pass")
                            :roles #{"admin"}}}))


;;check if user exists in fetched users
(defn user-exists? [username] 
  (if (not= 0 (count 
                (filter not-empty 
                        (cm/fetch :users :only {:_id false (keyword username) true})))) true false))


  
;;insert data for the user into db - collection users
(defn insert-user [username password roles]
  "Insert new user under condition that there isn't another one with same username."
  (if-not (user-exists? username)
    (cm/insert! :users
           {username {:username username
                            :password (creds/hash-bcrypt password)
                            :roles #{roles}}})))


;;get all users in users collection 
(defn get-all-users []
  (string-keys 
               (assoc-in (into {} 
                               (cm/fetch :users :only {:_id false})) [:user :roles] #{::user})))

;;get user by supplied username
(defn get-user [username]
  (filter not-empty (cm/fetch :users :only {:_id false (keyword username) true})))

;;delete user with supplied username
(defn delete-user [username]
  "Delete user with supplied username."
  (cm/destroy! :users {:username username}))


;;disconnect
(defn disconnect [conn]
  "Disconnect from database."
  (cm/close-connection conn))



(def local-users (atom {"user" {:username "user"
                            :password (creds/hash-bcrypt "pass")
                            :roles #{::user}}
                  "admin" {:username "admin"
                                  :password (creds/hash-bcrypt "pass")
                                  :roles #{::admin}}}))

(derive ::admin ::user)








