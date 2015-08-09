(ns twotris.model.app
  (:require [twotris.model.game :as game]) )


(def +difficulty=>tick-period+ {:hard 200, :normal 400, :easy 800})
(def +default-difficulty+ :normal)


(defn new-state [] {:ACTIVE false
                    :DIFFICULTY   +default-difficulty+
                    :GAME1 (assoc (game/new-game) :ref [:GAME1])
                    :GAME2 (assoc (game/new-game) :ref [:GAME2]) })


(let [gravity-if-game-not-done #(if (not (:DONE %)) (game/gravity %) %)]
  (defn gravity [APP]
    (-> APP
      (update-in [:GAME1] gravity-if-game-not-done)
      (update-in [:GAME2] gravity-if-game-not-done) )))


(defn clear-games [APP]
  (assoc APP :GAME1 (assoc (game/new-game) :ref [:GAME1])
             :GAME2 (assoc (game/new-game) :ref [:GAME2]) ))

(defn activate [APP]
  (assoc APP :ACTIVE true) )

(defn done? [STATUS]
  (= 2 STATUS) )

(declare done? status)

(defn app-done? [APP]
  (done? (:DONE (:GAME1 APP)) (:DONE (:GAME2 APP))) )

(defn app-status [APP]
  (status (:ACTIVE APP) (app-done? APP)) )

(defn done? [GAME1-DONE? GAME2-DONE?]
  (or GAME1-DONE? GAME2-DONE?) )

(defn status [ACTIVE? DONE?]
  (cond (and (not ACTIVE?) (not DONE?)) :ready
        (and ACTIVE?       (not DONE?)) :running
        (and ACTIVE?       DONE?)       :game-over ))

(defn running? [STATUS]
  (= :running STATUS) )

(defn ensure-tick-is-activated? [STATUS]
  (running? STATUS) )

(defn keydown-activated?? [STATUS]
  (running? STATUS) )

(declare score)

(defn app-score [APP]
  (score (:SCORE (:GAME1 APP)) (:SCORE (:GAME2 APP))) )


(defn score [GAME1-SCORE GAME2-SCORE]
  (+ GAME1-SCORE GAME2-SCORE) )

(def keyname=>action
  {"Z"     game/rotate
   "Q"     game/move-left
   "S"     game/drop-to-ground
   "D"     game/move-right
   "UP"    game/rotate
   "LEFT"  game/move-left
   "DOWN"  game/drop-to-ground
   "RIGHT" game/move-right })

(def keyname=>game
  {"Z"     :GAME1
   "Q"     :GAME1
   "S"     :GAME1
   "D"     :GAME1
   "UP"    :GAME2
   "LEFT"  :GAME2
   "DOWN"  :GAME2
   "RIGHT" :GAME2} )

(defn tick-period [DIFFICULTY]
  (DIFFICULTY +difficulty=>tick-period+) )

(defn app-tick-period [APP]
  (tick-period (:DIFFICULTY APP)) )






