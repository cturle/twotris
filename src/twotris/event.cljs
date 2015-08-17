(ns twotris.event
  (:require [clojure.set]
            [reagent.core  :as reagent]
            [twotris.model.app  :as app]
            [twotris.model.game :as game]
            [twotris.reaction   :as r] ))


(defonce +rr-app-state+ (atom {}))

(def <Key_Code>
  {:a-key  65
   :d-key  68
   :q-key  81
   :s-key  83
   :w-key  87
   :z-key  90
   :return-Key 13
   :up-key 38
   :left-key  37
   :down-key  40
   :right-key 39} )

(def <Code_Key> (clojure.set/map-invert <Key_Code>))

(defn on-app-keydown! [e]
  (let [KEY              (-> e .-keyCode <Code_Key>)
        [ACTION & ARGS]  ((deref (r/<r-app_r-keydown-fio> @+rr-app-state+)) KEY) ]
    ;(println "key pressed = " KEY)
    (when ACTION
      ;(println "key handled = " KEY ", APP-ACTION-CALL=" (cons ACTION ARGS))
      (.preventDefault e)
      (apply swap! @+rr-app-state+ (app/<Action_Updater> ACTION) ARGS) )))

(defn on-keydown! [e]
  (let [KEY            (-> e .-keyCode <Code_Key>)
        [GAME ACTION]  ((deref (r/<r-app_r-<Key_Game*Action>> @+rr-app-state+)) KEY) ]
    ;(println "key pressed = " KEY)
    (when (and GAME ACTION)
      ;(println "key handled = " KEY ", GAME=" GAME ", ACTION=" ACTION)
      (.preventDefault e)
      (swap! @+rr-app-state+ update GAME (app/<Action_Updater> ACTION)) )))

(let [r-activated (atom false)]

  (defn add-keydown! []
    (.addEventListener js/document "keydown" on-keydown!)
    (reset! r-activated true) )

  (defn remove-keydown! []
    (.removeEventListener js/document "keydown" on-keydown!)
    (reset! r-activated false) )

  (defn ensure-keydown-is-activated! []
    (when-not @r-activated
      (add-keydown!) ))

  (defn ensure-keydown-is-not-activated! []
    (when @r-activated
      (remove-keydown!) ))

  (defn on-keydown-activation! [ACTIVATION]
    (if ACTIVATION (ensure-keydown-is-activated!) (ensure-keydown-is-not-activated!)) )
)


(defn on-restart-button-click! [e]
  ;(println "on-restart-button-click event ...")
  (swap! @+rr-app-state+ app/clear-games) )


(defn on-start-button-click! [e]
  ;(println "on-start-button-click event ...")
  (swap! @+rr-app-state+ app/activate) )


(defn on-tick! []
  ;(println "on-tick event ..." (.getTime (js/Date.)))
  (when @(r/r-app-tick-activation @+rr-app-state+)
    (swap! @+rr-app-state+ app/gravity) ))


(let [r-current-period   (atom nil)
      r-current-timer-id (atom nil) ]

  (defn add-tick! [PERIOD]
    ;(println "add-tick!, PERIOD=" PERIOD)
    (when @r-current-timer-id  (throw (str "ERROR: add-tick, with timer-id = " @r-current-timer-id)))
    (reset! r-current-period   PERIOD)
    (reset! r-current-timer-id (js/setInterval on-tick! PERIOD)) )

  (defn remove-tick! []
    ;(println "remove-tick!")
    (when-not @r-current-timer-id (throw "ERROR: remove-tick!, with no timer-id"))
    (js/clearInterval @r-current-timer-id)
    (reset! r-current-period   nil)
    (reset! r-current-timer-id nil) )

  (defn ensure-tick-is-activated! [PERIOD]
    ;(println "ensure-tick-is-activated!, PERIOD=" PERIOD)
    (cond (and @r-current-timer-id (not= @r-current-period PERIOD))
          (do (remove-tick!)
              (add-tick! PERIOD) )
          (not @r-current-timer-id)
            (add-tick! PERIOD) ))

  (defn ensure-tick-is-not-activated! []
    ;(println "ensure-tick-is-not-activated!")
    (when @r-current-timer-id
      (remove-tick!) ))

)

(defonce init-permanent-handlers
  (do (.addEventListener js/document "keydown" on-app-keydown!) ))























