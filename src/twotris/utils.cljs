(ns twotris.utils
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :as reagent]) )


(def r-get
  (memoize
   (fn [R-ATOM PP]
     (reaction ;(println "computing r-get " PP " ...")
               (get @R-ATOM PP) ))))


(defn run-js-from-component! [F & ARGS]
  (js/setTimeout #(apply F ARGS) 0) )

