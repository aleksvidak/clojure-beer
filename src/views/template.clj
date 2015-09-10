(ns views.template
  (:use (compojure handler
                   [core :only (GET POST defroutes)]))
  (require [net.cgrand.enlive-html :as html]
           [ring.adapter.jetty :as jetty]
           [compojure.route :as route]))

;; Define the template
(html/deftemplate template-page "template.html"
  [post]
  [:title] (html/content (:title post))
  [:h1] (html/content (:title post))
  [:span.author] (html/content (:author post))
  [:div.post-body] (html/content (:body post)))

;; Some sample data
(def sample-post {:author "Aleksandar Vidakovic"
                  :title "Clojure TourPedia"
                  :body "Functional programming!"})

(reduce str (template-page sample-post))

(defroutes tourpedia-routes
  (GET "/home" [] (template-page sample-post))
  (route/not-found "<h1>Page not found</h1>"))

(defonce server (jetty/run-jetty #'tourpedia-routes {:port 8080 :join false}))