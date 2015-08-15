(ns twotris.utils
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :as reagent]) )


(def the (memoize identity))

; r-get = <r-Map*Index_r-Any>
(def r-get
  (memoize
   (fn [R-ATOM PP]
     (reaction ;(println "computing r-get " PP " ...")
               (the (get @R-ATOM PP)) ))))

(defn run-js-from-component! [F & ARGS]
  (js/setTimeout #(apply F ARGS) 0) )




