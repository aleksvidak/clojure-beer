(ns database.clj-db
   (:require [somnium.congomongo :as cm]
             [cemerick.friend.credentials :as creds]
             [ring.util.response :as resp]))

;;define connection to db beerdb 
(def connection
  (cm/make-connection "beerdb"
                     :host "127.0.0.1"
                     :port 27017))

;;set connection globally
(cm/set-connection! connection)
(cm/set-write-concern connection :strict)

;;disconnect
(defn disconnect [conn]
  "Disconnect from database."
  (cm/close-connection conn))

;;initial admin username "admin", password "pass"
(defn insert-admin []
  "Initially insert user with admin role to be able to use the application."
  (cm/insert! :users 
              {:username "admin"
                            :password (creds/hash-bcrypt "pass")
                            :roles #{"admin"}}))

;;fetch users from database
(def users (atom 
               (into {}(let [map (cm/fetch :users :only {:_id false})
                 users {}]
               (for [x map]  
                 (if (= ["user"] (get x :roles))
                 (assoc users  (:username (into {} x)) (assoc (into {} x) :roles #{::user}))
                 (assoc users  (:username (into {} x)) (assoc (into {} x) :roles #{::admin}))))))))

;;to bypass restriction of user pages to admin
(derive ::admin ::user)


;;check if user exists in fetched users
(defn user-exists? [username]
  "Check if user with supplied username exists in the database."
  (if (not= 0 (count 
                (filter not-empty 
                        (cm/fetch :users :only {:_id false} :where {:username username})))) true false))


  
;;insert data for the user into db - collection users
(defn insert-user [username password role]
  "Insert new user under condition that there isn't another one with same username."
  (if-not (user-exists? username)
    (do (cm/insert! :users 
              {:username username
                            :password (creds/hash-bcrypt password)
                            :roles #{role}}) "User exists!"
      (resp/redirect "/admin"))
    (resp/redirect "/adminb")))


;;get all users in users collection 
(defn get-all-users []
  "Get all users in :users collection from mongodb."
  (cm/fetch :users :only {:_id false :password false}))

;;get user by supplied username
(defn get-user [username]
  (filter not-empty (cm/fetch :users :only {:_id false} :where {:username username})))

;;update user data
(defn update-user [username password role]
  "Update user password & role."
             (let [old-user (cm/fetch-one :users :where {:username username})]
               (cm/update! :users old-user (merge old-user {:password (creds/hash-bcrypt password) :roles #{role}}))))

;;delete user with supplied username
(defn delete-user [username]
  "Delete user with supplied username."
  (cm/destroy! :users {:username username}))





