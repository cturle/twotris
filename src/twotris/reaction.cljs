(ns twotris.reaction
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [twotris.utils :as u]
            [twotris.model.app :as app]
            [reagent.core :as reagent]) )


;; add reactions of model needed by views. sort of cursors.

(def r-app-done?
  (memoize
   (fn [R-APP]
     (let [R-GAME1-DONE? (reduce u/r-get R-APP [:GAME1 :DONE])
           R-GAME2-DONE? (reduce u/r-get R-APP [:GAME2 :DONE]) ]
       (reaction ;(println "computing r-app-done? ...")
                 (app/done? @R-GAME1-DONE? @R-GAME2-DONE?) )))))

(def r-app-status
  (memoize
   (fn [R-APP]
     (let [R-APP-ACTIVE? (u/r-get R-APP :ACTIVE)
           R-APP-DONE?   (r-app-done? R-APP) ]
       (reaction ;(println "computing r-app-status ...")
                 (app/status @R-APP-ACTIVE? @R-APP-DONE?) )))))

(def r-app-running?
  (memoize
   (fn [R-APP]
     (let [R-APP-STATUS (r-app-status R-APP)]
       (reaction ;(println "computing r-app-running ...")
                 (app/running? @R-APP-STATUS) )))))

(def r-app-tick-activation
  (memoize
   (fn [R-APP]
     (let [R-APP-RUNNING? (r-app-running? R-APP)]
       (reaction ;(println "computing r-app-tick-activation ...")
                 @R-APP-RUNNING? )))))

(def r-app-score
  (memoize
   (fn [R-APP]
     (let [R-GAME1-SCORE (reduce u/r-get R-APP [:GAME1 :SCORE])
           R-GAME2-SCORE (reduce u/r-get R-APP [:GAME2 :SCORE]) ]
       (reaction ;(println "computing r-app-score ...")
                 (app/score @R-GAME1-SCORE @R-GAME2-SCORE) )))))


(def r-app-keydown-activated??
  (memoize
   (fn [R-APP]
     (let [R-APP-STATUS (r-app-status R-APP)]
       (reaction ;(println "computing r-app-keydown-activated?? ...")
                 (app/keydown-activated?? @R-APP-STATUS) )))))

