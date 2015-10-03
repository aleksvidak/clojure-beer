(ns start
  (:require [views.routing :as route]
            [somnium.congomongo :as cm]))


(defn -main [& args]
     (route/start-server))