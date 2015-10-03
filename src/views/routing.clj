(ns views.routing
  (:use (compojure handler
                   [core :only (GET POST ANY defroutes) :as compojure]))
  (:require [net.cgrand.enlive-html :as html]
            [ring.adapter.jetty :only [run-jetty]]
            [ring.util.response :as resp]
            [ring.middleware.session :as session]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [cemerick.friend :as friend]
            [cemerick.friend.credentials :as creds]
            [cemerick.friend.workflows :as workflows]
            [database.clj-dbusers :as users :refer (users)]
            [database.clj-dbbeers :as dbb]
            [clojure-beer.metrics :as metrics]
            [views.templates :as templates]))

;;routing
(defroutes beer-routes
  (GET "/login" [] (html/emit* (templates/show-login)))
  (GET "/" request 
      (if (= #{::users/user} 
             (:roles (friend/current-authentication))) 
        (resp/redirect "/index")
        (resp/redirect "/admin")))

  (GET "/index" request
      (friend/authorize #{::users/user} (html/emit* 
                                          (templates/show-index 
                                            (:current (friend/identity request))
                                            (into [] 
                                                  (sort-by str (dbb/get-beers)))))))
  (GET "/admin" request 
       (friend/authorize #{::users/admin} (html/emit* (templates/show-adminu (into [] 
                                                      (users/get-all-users))))))
  (GET "/adminb" request 
       (friend/authorize #{::users/admin} (html/emit* 
                                          (templates/show-adminb (into [] 
                                                      (take 1000 (sort-by str (dbb/get-beers))))))))
  (GET "/about" request 
        (html/emit* (templates/show-about)))
  (POST "/addBeer" request 
        (let [beer (get (:params request) :beername)]
      (dbb/insert-beer beer)))
  (POST "/deleteBeer" request 
        (let [beer (get (:params request) :beername)]
      (dbb/delete-beer beer)))
  (POST "/addUser" request 
        (let [username (get (:params request) :username)
              password (get (:params request) :password)
              role (get (:params request) :role)]
      (do
        (users/insert-user username password role)
        (users/update-users))))
   (POST "/editUser" request 
        (let [username (get (:params request) :username)
             role (get (:params request) :role)]
      (do (users/update-user username role)
        (users/update-users))))
   (POST "/deleteUser" request 
        (let [username (get (:params request) :username)]
        (users/delete-user username)
        (users/update-users)))
   (POST "/reset" request 
        (let [username (get (:params request) :username)
              password (get (:params request) :password)]
        (users/reset-password username password)
        (users/update-users)))
   (POST "/addReview" request 
        (let [username (get (:params request) :username)
              beer (get (:params request) :beer)
              score (get (:params request) :score)]
        (dbb/insert-new-review username beer score)))
   (GET "/recommend" request 
       (friend/authorize #{::users/user} (html/emit* (templates/show-recommendations (:current (friend/identity request))                                                                              
                                                                                      (metrics/recommend (:current (friend/identity request)))                                                                          
                                                                                      (metrics/sort-filter-similar-users (:current (friend/identity request)))))))
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




(defn start-server []
  (ring.adapter.jetty/run-jetty #'app {:port 8080 :join? false}))
