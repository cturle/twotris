(ns twotris.model.app
  (:require [clojure.set]
            [twotris.model.game :as game]) )

;;; === Parameters ===

(def +<Keyboard_action-key>-map+
  {:azerty-kb
            {[:GAME1 :rotate-action] :z-key
             [:GAME1 :left-action]   :q-key
             [:GAME1 :drop-action]   :s-key
             [:GAME1 :right-action]  :d-key
             [:GAME2 :rotate-action] :up-key
             [:GAME2 :left-action]   :left-key
             [:GAME2 :drop-action]   :down-key
             [:GAME2 :right-action]  :right-key }
   :qwerty-kb
            {[:GAME1 :rotate-action] :w-key
             [:GAME1 :left-action]   :a-key
             [:GAME1 :drop-action]   :s-key
             [:GAME1 :right-action]  :d-key
             [:GAME2 :rotate-action] :up-key
             [:GAME2 :left-action]   :left-key
             [:GAME2 :drop-action]   :down-key
             [:GAME2 :right-action]  :right-key }} )

(def +default-difficulty+ :normal)

(def +default-keyboard+ :azerty-kb)

(def +difficulty=>tick-period+ {:hard 250, :normal 500, :easy 1000})

(def keydown
  {:ready
      {:return-Key [:activate-AppAction]}
   :running
      {}
   :game-over
      {:return-Key [:clear-games-AppAction]} })


;;; === ===

(defn new-state [] {:ACTIVE      false
                    :DIFFICULTY  +default-difficulty+
                    :KEYBOARD    +default-keyboard+
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

(defn tick-period [DIFFICULTY]
  (DIFFICULTY +difficulty=>tick-period+) )

(defn app-tick-period [APP]
  (tick-period (:DIFFICULTY APP)) )

(def <Action_Updater>
  {:rotate-action  game/rotate
   :left-action    game/move-left
   :drop-action    game/drop-to-ground
   :right-action   game/move-right
   :activate-AppAction    activate
   :clear-games-AppAction clear-games })

(def <Keyboard_action-key> +<Keyboard_action-key>-map+)

(def <action-key_<Key_Game*Action>> clojure.set/map-invert)

(let [KEYBOARD=>KEYBOARD {:azerty-kb :qwerty-kb, :qwerty-kb :azerty-kb}]
  (defn other-keyboard [KEYBOARD]
    (get KEYBOARD=>KEYBOARD KEYBOARD) ))






