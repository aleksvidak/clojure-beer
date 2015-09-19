(ns views.template
  (:use (compojure handler
                   [core :only (GET POST defroutes)]))
  (require [net.cgrand.enlive-html :as html]
           [ring.adapter.jetty :as jetty]
           [compojure.route :as route]))

;; Define the template
(defn get-page [page post]
  (html/deftemplate template-page page
  [post]
  [:title] (html/content (:title post))
  [:h1] (html/content (:title post))
  [:span.author] (html/content (:author post))
  [:div.post-body] (html/content (:body post))))

;; Some sample data
(def sample-post {:author "Aleksandar Vidakovic"
                  :title "Clojure TourPedia"
                  :body "Functional programming!"})

;;(reduce str (template-page sample-post))

(defroutes beer-routes
  (GET "/login" [] (get-page "public/login.html" sample-post))
  (GET "/forgot" [] (get-page "public/forgotpass.html" sample-post))
  (GET "/registration" [] (get-page "public/registration.html" sample-post))
  (GET "/index" [] (get-page "public/index.html" sample-post))
  (route/resources "/")
  (route/not-found "<h1>Page not found</h1>")
  )

(defonce server (jetty/run-jetty #'beer-routes {:port 8080 :join false}))