(ns twotris.reaction
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [twotris.utils :as u ];:refer [the]]
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

(def r-app-tick-activation r-app-running?)

(def r-app-keydown-activation r-app-running?)

(def r-app-score
  (memoize
   (fn [R-APP]
     (let [R-GAME1-SCORE (reduce u/r-get R-APP [:GAME1 :SCORE])
           R-GAME2-SCORE (reduce u/r-get R-APP [:GAME2 :SCORE]) ]
       (reaction ;(println "computing r-app-score ...")
                 (app/score @R-GAME1-SCORE @R-GAME2-SCORE) )))))

(def r-app-tick-period
  (memoize
   (fn [R-APP]
     (let [R-DIFFICULTY (u/r-get R-APP :DIFFICULTY)]
       (reaction ;(println "computing r-app-tick-period ...")
                 (app/tick-period @R-DIFFICULTY) )))))


(def <r-app_r-action-key>
  (memoize
    (fn [R-APP]
      ;(println "init <r-app_r-action-key>.")
      (let [R-KEYBOARD (u/r-get R-APP :KEYBOARD)]
        (reaction ;(println "computing <r-app_r-action-key>. KEYBOARD=" @R-KEYBOARD)
                  (u/the (app/<Keyboard_action-key> @R-KEYBOARD)) )))))

(def <r-app_r-<Key_Game*Action>>
  (memoize
    (fn [R-APP]
      ;(println "init <r-app_r-<Key_Game*Action>>.")
      (let [R-ACTION-KEY (<r-app_r-action-key> R-APP)]
        (reaction ;(println "computing <r-app_r-<Key_Game*Action>>.")
                  (u/the (app/<action-key_<Key_Game*Action>> @R-ACTION-KEY)) )))))

(def <r-app*Game*Action_r-Key>
  (memoize
   (fn [R-APP GAME-ID ACTION]
     ;(println "init <r-app*Game*Action_r-Key>. GAME-ID=" GAME-ID ", ACTION=" ACTION)
     (let [R-ACTION-KEY (<r-app_r-action-key> R-APP)]
       (reaction ;(println "computing <r-app*Game*Action_r-Key>. GAME-ID=" GAME-ID ", ACTION=" ACTION)
                 (u/the (@R-ACTION-KEY [GAME-ID ACTION])) )))))

(def <r-app_r-keydown>
  (memoize
    (fn [R-APP]
      ;(println "init <r-app_r-keydown>.")
      (let [R-APP-STATUS (r-app-status R-APP)]
        (reaction ;(println "computing <r-app_r-keydown>")
                  (app/keydown @R-APP-STATUS) )))))

(def <r-app_r-keydown-fio>
  (memoize
    (fn [R-APP]
      ;(println "init <r-app_r-keydown-fio>.")
      (let [R-KEYDOWN (<r-app_r-keydown> R-APP)]
        (reaction ;(println "computing <r-app_r-keydown-fio>")
                  (@R-KEYDOWN :fio) )))))
































