(ns twotris.view
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core       :as reagent]
            [twotris.utils      :as u]
            [twotris.reaction   :as r]
            [twotris.event      :as e]
            [twotris.model.game :as game]
            [twotris.model.app  :as app]
            [clojure.string     :as string] ))



;;; === Parameters===
(def +action-name-map+
  {:rotate-action "rotate"
   :left-action   "move left"
   :drop-action   "drop down"
   :right-action  "move right" })

(def +block-size+ 50)

(def +colors+
  ["#ff0000", "#00ff00", "#0000ff", "#AB4642", "#DC9656", "#F7CA88", "#A1B56C", "#86C1B9", "#7CAFC2", "#BA8BAF", "#A16946"])

(def +command-action-vec+
  [:rotate-action, :left-action, :drop-action, :right-action])

(def +keyboard=>bt-label-map+
  {:azerty-kb "GO QWERTY !", :qwerty-kb "GO AZERTY !"} )

(def +key-name-map+
  {:a-key     "A"
   :d-key     "D"
   :q-key     "Q"
   :s-key     "S"
   :w-key     "W"
   :z-key     "Z"
   :up-key    "UP"
   :left-key  "LEFT"
   :down-key  "DOWN"
   :right-key "RIGHT" })

(def +status+
  {:ready     "Ready !"
   :running   "Playing ..."
   :game-over "Game Over"} )



;;; === Accessors ===

(defn action-name [ACTION]
  (get +action-name-map+ ACTION) )

(defn key-name [KEY]
  (get +key-name-map+ KEY) )



;;; === -VIEW ===
(declare app-score-view
         app-status-view
         game-view
         left-info-view
         keydown-activation-watch
         keydown-configuration-watch
         restart-button-view
         right-info-view
         start-button-view
         tick-activation-watch
         tick-period-watch )

(defn app-view [R-APP]
  ;(println "init app-view. R-APP=" R-APP)
  (let [R-GAME1         (u/r-get R-APP :GAME1)
        R-GAME2         (u/r-get R-APP :GAME2)
        R-APP-STATUS    (r/r-app-status R-APP) ]
    (fn []
      ;(println "rendering app-view")
      [:div
        [app-status-view R-APP]
        [:div.games
          [left-info-view R-APP] [game-view R-GAME1] [game-view R-GAME2] [right-info-view R-APP] ]
        [app-score-view R-APP]
        (case @R-APP-STATUS
          :ready      [start-button-view]
          :game-over  [restart-button-view]
          nil )
        [tick-activation-watch R-APP]
        [keydown-activation-watch R-APP]
       ]
     )))


(defn app-status-view [R-APP]
  (let [R-APP-STATUS  (r/r-app-status R-APP)]
    ;(println "init app-status-view ...")
    (fn []
      ;(println "rendering app-status-view ...")
      [(if (= :game-over @R-APP-STATUS) :div.app_red_infos :div.app_infos) (get +status+ @R-APP-STATUS "status?")] )))


(declare command-info-view
         parameters-view )

(defn left-info-view [R-APP]
  [:div.infos
   [command-info-view R-APP :GAME1]
   [parameters-view R-APP] ])


(defn command-info-view [R-APP GAME-ID]
  ;(println "init command-info-view. GAME-ID=" GAME-ID)
  (let [ACTION-vec  +command-action-vec+
        R-KEY-vec   (mapv #(r/<r-app*Game*Action_r-Key> R-APP GAME-ID %) ACTION-vec)
        LI-VIEW     (fn [ACTION R-KEY] [:li (str "'" (key-name @R-KEY) "' : " (action-name ACTION))]) ]
    (fn []
      ;(println "process command-info-view. GAME-ID=" GAME-ID)
      [:div.command_infos
        [:p "Commands :"]
        (into [:ul] (map LI-VIEW ACTION-vec R-KEY-vec) )])))


(declare difficulty-view
         keyboard-view )

(defn parameters-view [R-APP]
  ;(println "init parameters-view ...")
  [:div.parameters
   [difficulty-view R-APP]
   [keyboard-view R-APP] ])

(defn difficulty-view [R-APP]
  ;(println "init difficulty-view ...")
  (let [R-DIFFICULTY (u/r-get R-APP :DIFFICULTY)]
    (fn []
      ;(println "process difficulty-view ...")
      [:div.difficulty
        [:text "Difficulty = "] [:select {:on-change #(swap! R-APP assoc :DIFFICULTY (keyword (-> % .-target .-value)))
                                          :value @R-DIFFICULTY }
                                  [:option {:value :hard  }  "Hard"]
                                  [:option {:value :normal}  "Normal"]
                                  [:option {:value :easy  }  "Easy"] ]] )))

(defn keyboard-view [R-APP]
  ;(println "init keyboard-view ...")
  (let [R-KEYBOARD  (u/r-get R-APP :KEYBOARD)]
    (fn []
      ;(println "process keyboard-view ...")
      [:div.keyboard
       [:button.param_button {:on-click #(swap! R-APP update :KEYBOARD app/other-keyboard)}
                           (get +keyboard=>bt-label-map+ @R-KEYBOARD) ]])))


(declare game-board-graphic-view
         game-score-view )

(defn game-view [R-GAME]
  ;(println "init & rendering game-view, ref=" (:ref @R-GAME))
  [:div.game
    [game-board-graphic-view R-GAME]
    ;[game-score-view R-GAME]
  ])


(declare block)

(defn game-board-graphic-view [R-GAME]
  (let [{:keys [PIECE COLOR X Y BLOCK-PILE DONE]} @R-GAME
        PIECE-WIDTH   (count PIECE)
        PIECE-HEIGHT  (count (first PIECE))
        BLOCK-WIDTH   (count BLOCK-PILE)
        BLOCK-HEIGHT  (count (first BLOCK-PILE)) ]
    ;(println "computing game-board-graphic-view, ref=" (:ref @R-GAME))
    [:svg.board {:style    {:width 200, :height 400}
                 :view-box (string/join " " [0 0 10 20])}
      (when-not DONE
        (into [:g {:name "current piece"}]
              (for [I (range PIECE-WIDTH)
                    J (range PIECE-HEIGHT)
                    :when (pos? (get-in PIECE [I J])) ]
                [block (+ X I) (+ Y J) COLOR] )))
      (into [:g {:name "block pile"}]
            (for [I (range BLOCK-WIDTH)
                  J (range BLOCK-HEIGHT)
                  :let [BLOCK-COLOR (get-in BLOCK-PILE [I J])]
                  :when (not (neg? BLOCK-COLOR))]
              [block I J BLOCK-COLOR] ))]))

(defn right-info-view [R-APP]
  [:div.infos
   [command-info-view R-APP :GAME2] ])


(declare color-view)

(defn block [X Y COLOR]
  [:rect {:x            X
          :y            Y
          :width        1
          :height       1
          :stroke       "black"
          :stroke-width 0.01
          :rx           0.1
          :fill         (color-view COLOR) }])


(defn color-view [COLOR]
  (nth +colors+ COLOR) )


(defn app-score-view [R-APP]
  ;(println "init app-score-view ...")
  (let [R-APP-SCORE  (r/r-app-score  R-APP)
        R-APP-STATUS (r/r-app-status  R-APP) ]
    (fn []
      ;(println "rendering app-score-view ...")
      [(if (= :game-over @R-APP-STATUS) :div.app_red_infos :div.app_infos) (str "Score = " @R-APP-SCORE)] )))


; actually not used
(defn game-score-view [R-GAME]
  ;(println "init game-score-view ...")
  (let [R-GAME-SCORE  (u/r-get R-GAME :SCORE)
        GAME-REF      (:ref @R-GAME) ]
    (fn []
      ;(println "rendering game-score-view, ref=" GAME-REF)
      [:h2 (str @R-GAME-SCORE)] )))


(defn restart-button-view []
  ;(println "rendering restart-button-view ...")
  [:button.app_button {:on-click e/on-restart-button-click!}
                      "RESTART !"] )


(defn start-button-view []
  ;(println "rendering start-button-view ...")
  [:button.app_button {:on-click e/on-start-button-click!}
                      "START !"] )



;;; === -WATCH ===

(defn tick-activation-watch [R-APP]
  ;(println "init/process tick-activation-watch ...")
  (let [R-ACTIVATION (r/r-app-tick-activation R-APP)]
    (u/run-js-from-component! e/on-tick-activation! @R-ACTIVATION)
    [:div.watch {:name "tick-activation-watch"}
      (when @R-ACTIVATION
        [tick-period-watch R-APP] )] ))


(defn tick-period-watch [R-APP]
  ;(println "init/process tick-period-watch ...")
  (u/run-js-from-component! e/on-tick-period! @(r/r-app-tick-period R-APP))
  [:div.watch {:name (str "tick-period-watch!")}] )


(defn keydown-activation-watch [R-APP]
  ;(println "init/process keydown-activation-watch ...")
  (let [R-ACTIVATION (r/r-app-keydown-activation R-APP)]
    (u/run-js-from-component! e/on-keydown-activation! @R-ACTIVATION)
    [:div.watch {:name "keydown-activation-watch"}
       (when @R-ACTIVATION
         [keydown-configuration-watch R-APP] )] ))


(defn keydown-configuration-watch [R-APP]
  ;(println "init/process keydown-configuration-watch ...")
  (deref (r/<r-app_r-<Key_Game*Action>> R-APP))
  [:div.watch {:name "keydown-configuration-watch"}] )














