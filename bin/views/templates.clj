(ns views.templates
  (:require [net.cgrand.enlive-html :as html]))


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
           (html/clone-for [beer beers] (html/content (str beer)))))

(defn show-adminu [users]
  (html/at (html/html-resource "public/adminu.html")
           [:table :tbody :tr]
           (html/clone-for [user users]                           
                           [:td#un] (html/content (:username user))
                           [:td#rl] (html/content (:roles user)))))

(defn show-about []
  (html/at (html/html-resource "public/about.html")))

(defn show-recommendations [user beers reviewers]
  (html/at (html/html-resource "public/recommendations.html")
           [:h3#current-user] (html/content user)
           [:ol.beer-list [:li html/first-of-type]]
           (html/clone-for [beer beers] (html/content (apply str (interpose " " beer))))
           [:ol.buddies-list [:li html/first-of-type]]
           (html/clone-for [reviewer reviewers] (html/content (apply str (interpose " " reviewer))))))







