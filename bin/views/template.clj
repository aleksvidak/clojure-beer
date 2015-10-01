(ns views.template
  (:use (compojure handler
                   [core :only (GET POST ANY defroutes) :as compojure]))
  (:require [net.cgrand.enlive-html :as html]
            [ring.adapter.jetty :as jetty]
            [ring.util.response :as resp]
            ring.middleware.session
            [compojure.route :as route]
            [compojure.handler :as handler]
            [cemerick.friend :as friend]
            [cemerick.friend.credentials :as creds]
            [cemerick.friend.workflows :as workflows]
            [database.clj-db :as users :refer (users)]
            [input.csv-dataset :as ds]))


(defn show-index [user beers]
  (html/at (html/html-resource "public/index.html")
      [:select.selectpicker [:option html/first-of-type]] 
      (html/clone-for [beer beers] (html/content beer))
      [:h3#current-user] (html/content user)))

(defn show-login []
  (html/at (html/html-resource "public/login.html")))


(defn show-adminb [beers]
  (html/at (html/html-resource "public/adminb.html")
           [:select.selectpicker [:option html/first-of-type]]
           (html/clone-for [beer beers] (html/content beer))))

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
  (GET "/login" [] (html/emit* (show-login)))
  (GET "/" request 
      (if (= #{::users/user} 
             (:roles (friend/current-authentication))) 
        (resp/redirect "/index")
        (resp/redirect "/admin")))

  (GET "/index" request
      (friend/authorize #{::users/user} (html/emit* 
                                          (show-index 
                                            (:current (friend/identity request))
                                            (into [] 
                                                  (sort-by str (ds/get-beers)))))))
  (GET "/admin" request 
       (friend/authorize #{::users/admin} (html/emit* (show-adminu (into [] 
                                                      (database.clj-db/get-all-users))))))
  (GET "/adminb" request 
       (friend/authorize #{::users/admin} (html/emit* 
                                          (show-adminb (into [] 
                                                      (take 1000 (sort-by str (ds/get-beers))))))))
  (GET "/about" request 
       (friend/authorize #{::users/user} (html/emit* (show-about))))
  (POST "/addBeer" request 
        (let [beer (get (:params request) :beername)]
      (ds/insert-beer beer)))
  (POST "/deleteBeer" request 
        (let [beer (get (:params request) :beername)]
      (ds/delete-beer beer)))
  (POST "/addUser" request 
        (let [username (get (:params request) :username)
              password (get (:params request) :password)
              role (get (:params request) :role)]
      (database.clj-db/insert-user username password role)))
   (POST "/editUser" request 
        (let [username (get (:params request) :username)
             role (get (:params request) :role)]
      (do (database.clj-db/update-user username role)
        (database.clj-db/update-users))))
   (POST "/deleteUser" request 
        (let [username (get (:params request) :username)]
        (database.clj-db/delete-user username)))
   (POST "/reset" request 
        (let [username (get (:params request) :username)
              password (get (:params request) :password)]
        (database.clj-db/reset-password username password)
        (database.clj-db/update-users)))
   (POST "/addReview" request 
        (let [username (get (:params request) :username)
              beer (get (:params request) :beer)
              score (get (:params request) :score)]
        (ds/insert-new-review username beer score)))
  (route/resources "/")
  (friend/logout (ANY "/logout" request (resp/redirect "/login")))
  (route/not-found "<h1>Page not found</h1>"))


(def app
  (-> (handler/site
       (friend/authenticate beer-routes
       			{:allow-anon? true                                    
             :credential-fn #(creds/bcrypt-credential-fn @users %)
             :workflows [(workflows/interactive-form)]}))
      (session/wrap-session)))

(.stop server)

(.start server)


(defonce server (jetty/run-jetty #'app {:port 8080 :join? false}))




