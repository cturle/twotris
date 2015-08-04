(ns twotris.core
    (:require [twotris.event :as e]
              [twotris.view  :as v]
              [twotris.model.app :as app]
              [reagent.core  :as reagent]))

(enable-console-print!)
;(println "core file loading ...")

(defonce +r-app-state+ (reagent/atom {}))

(defn on-js-reload []
  ;(println "js reloading ...")
  (reset! +r-app-state+ (app/new-state))
  (reagent/render-component [v/app-view +r-app-state+] (. js/document (getElementById "app")))
  (e/init-handlers +r-app-state+)
  ;(println "... js reloaded !")
  :loaded
)


(defonce start
  (on-js-reload) )

;(println "core file loaded ...")
