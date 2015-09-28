(ns views.template
  (:use (compojure handler
                   [core :only (GET POST ANY defroutes) :as compojure]))
  (:require [net.cgrand.enlive-html :as html]
            [ring.adapter.jetty :as jetty]
            [ring.util.response :as resp]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [cemerick.friend :as friend]
            [cemerick.friend.credentials :as creds]
            [cemerick.friend.workflows :as workflows]
            [database.clj-db :as users :refer (users)]
            [input.csv-dataset :as ds]))


(defn show [things]
  (html/at (html/html-resource "public/index.html")
      [:select.beers [:option html/first-of-type]] 
      (html/clone-for [thing things] (html/content thing))))

(defn show-login []
  (html/at (html/html-resource "public/login.html")))


(defn show-adminb []
  (html/at (html/html-resource "public/adminb.html")))

(defn show-adminu [users]
  (html/at (html/html-resource "public/adminu.html")
           [:table :tbody :tr]
           (html/clone-for [user users]                           
                           [:td#un] (html/content (:username user))
                           [:td#rl] (html/content (:roles user)))))

(defn show-about []
  (html/at (html/html-resource "public/about.html")))

;;routing
(defroutes beer-routes
  (GET "/" request 
      (friend/authorize #{::users/user} (html/emit* 
                                          (show (into [] 
                                                      (sort-by str ds/allbeers))))))
  (GET "/admin" request 
       (friend/authorize #{::users/admin} (html/emit* (show-adminu (into [] 
                                                      (database.clj-db/get-all-users))))))
  (GET "/adminb" request 
       (friend/authorize #{::users/admin} (html/emit* (show-adminb))))
  (GET "/about" request 
       (friend/authorize #{::users/user} (html/emit* (show-about))))
  (GET "/login" [] (html/emit* (show-login)))
  (POST "/addBeer" request 
        (let [beer-name (get (:params request) :beername)]
      (str "Name of the beer: " beer-name)))
  (POST "/addUser" request 
        (let [username (get (:params request) :username)
              password (get (:params request) :password)
              role (get (:params request) :role)]
      (database.clj-db/insert-user username password role)))
    (POST "/editUser" request 
        (let [username (get (:params request) :username)]
      (database.clj-db/update-user)))
    (POST "/deleteUser" request 
        (let [username (get (:params request) :username)]
      (database.clj-db/delete-user username)))
  (route/resources "/")
  (friend/logout (ANY "/logout" request (resp/redirect "/login")))
  (route/not-found "<h1>Page not found</h1>"))


(def app
  (handler/site
   (friend/authenticate beer-routes
   			{:allow-anon? true
         :default-landing-uri "/"                                      
         :credential-fn #(creds/bcrypt-credential-fn @users %)
         :workflows [(workflows/interactive-form)]})))


(defonce server (jetty/run-jetty #'app {:port 8080 :join? false}))

(.stop server)

(.start server)
