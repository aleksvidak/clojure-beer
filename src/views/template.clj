(ns views.template
  (:use (compojure handler
                   [core :only (GET POST ANY defroutes) :as compojure]))
  (:require [net.cgrand.enlive-html :as html]
            [ring.adapter.jetty]
            [ring.util.response :as resp]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [cemerick.friend :as friend]
            [cemerick.friend.credentials :as creds]
            [cemerick.friend.workflows :as workflows]
            [server.clj-db :as users :refer (local-users)]
            [input.csv-dataset :as ds]))


(def test {:a 1 :b 3 :c 3})
;; Define the template
  (html/deftemplate template-page "public/index.html"
  
  [tes]
  [:title] (html/content  "Welcome to beer rating site.")
  [:ul.beers [:li html/first-of-type]] (html/clone-for [a tes]
                                                 [:li :a] (html/content a)))

;; Some sample data
(def sample-post {:author "Aleksandar Vidakovic"
                  :title "Clojure Beer"
                  :content "Functional programming!"})

;;(reduce str (template-page sample-post))


;;routing
(defroutes beer-routes
  (GET "/" request 
       (friend/authorize #{::users/user} (template-page test)))
 (GET "/admin" request
       "Login" )
  (GET "/login" [] (template-page (map keys (ds/get-all-reviews 1))))
  (route/resources "/")
  (friend/logout (ANY "/logout" request (resp/redirect "/login")))
  (route/not-found "<h1>Page not found</h1>"))


(def app
  (handler/site
   (friend/authenticate beer-routes
   			{:allow-anon? true
         :default-landing-uri "/"                                      
         :credential-fn #(creds/bcrypt-credential-fn @local-users %)
         :workflows [(workflows/interactive-form)]})))


(defonce server (ring.adapter.jetty/run-jetty #'app {:port 8080 :join? false}))

(.stop server)

(.start server)
(map vals (ds/get-all-reviews 5))
