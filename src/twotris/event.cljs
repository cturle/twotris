(ns twotris.event
  (:require [clojure.set]
            [reagent.core  :as reagent]
            [twotris.model.app :as app]
            [twotris.model.game :as game] ))


(defonce +rr-app-state+ (atom {}))

(def key=>code
  {"Z"     90
   "Q"     81
   "S"     83
   "D"     68
   "UP"    38
   "LEFT"  37
   "DOWN"  40
   "RIGHT" 39} )

(def keycode=>keyname (clojure.set/map-invert key=>code))

(defn on-keydown [e]
  (let [KEYNAME (-> e .-keyCode keycode=>keyname)
        GAME    (app/keyname=>game KEYNAME)
        ACTION  (app/keyname=>action KEYNAME) ]
    ;(println "key pressed = " KEYNAME)
    (when (and GAME ACTION)
      (.preventDefault e)
      (swap! @+rr-app-state+ update-in [GAME] ACTION) )))

(let [r-activated (atom false)]

  (defn add-keydown! []
    (.addEventListener js/document "keydown" on-keydown)
    (reset! r-activated true) )

  (defn remove-keydown! []
    (.removeEventListener js/document "keydown" on-keydown)
    (reset! r-activated false) )

  (defn ensure-keydown-is-activated! []
    (when-not @r-activated
      (add-keydown!) ))

  (defn ensure-keydown-is-not-activated! []
    (when @r-activated
      (remove-keydown!) ))
  )


(defn on-restart-button-click [e]
  ;(println "on-restart-button-click event ...")
  (swap! @+rr-app-state+ app/clear-games) )


(defn on-start-button-click [e]
  ;(println "on-start-button-click event ...")
  (swap! @+rr-app-state+ app/activate) )


(defn on-tick []
  (when (not= :running (app/app-status @@+rr-app-state+)) (println "ERROR: on-tick while not running."))
  ;(println "on-tick event ..." (.getTime (js/Date.)))
  (swap! @+rr-app-state+ app/gravity) )


(let [r-current-period   (atom nil)
      r-current-timer-id (atom nil) ]

  (defn add-tick! []
    ;(println "add-tick! ...")
    (when @r-current-timer-id (println "ERROR: add-tick, with timer-id = " @r-current-timer-id))
    (reset! r-current-period   (:TICK-PERIOD @@+rr-app-state+))
    (reset! r-current-timer-id (js/setInterval on-tick @r-current-period)) )

  (defn remove-tick! []
    ;(println "remove-tick! ...")
    (if-not @r-current-timer-id
      (println "ERROR: remove-tick!, with no timer-id")
      (do (js/clearInterval @r-current-timer-id)
          (reset! r-current-period   nil)
          (reset! r-current-timer-id nil) )))

  (defn ensure-tick-is-activated! []
    ;(println "ensure-tick-is-activated! ...")
    (cond (and @r-current-timer-id (not= @r-current-period (:TICK-PERIOD @@+rr-app-state+)))
          (do (remove-tick!)
              (add-tick!) )
          (not @r-current-timer-id)
            (add-tick!) ))

  (defn ensure-tick-is-not-activated! []
    ;(println "ensure-tick-is-not-activated! ...")
    (when @r-current-timer-id
      (remove-tick!) ))

  (defn ensure-tick-activation! [ACTIVATION]
    (if ACTIVATION (ensure-tick-is-activated!) (ensure-tick-is-not-activated!)) )

  (defn ensure-tick-period! []
    ;(println "ensure-tick-period! ...")
    (when @r-current-timer-id
      (when (not= @r-current-period (:TICK-PERIOD @@+rr-app-state+))
        (remove-tick!)
        (add-tick!) )))
)





























