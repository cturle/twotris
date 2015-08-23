(ns twotris.model.game
  (:require [twotris.orto.world :as ow]) )

(def +pieces+
  [[[0 0 0 0]
    [0 0 0 0]
    [1 1 1 1]
    [0 0 0 0]
    [0 0 0 0]]
   [[1 1]
    [1 1]]
   [[1 0]
    [1 1]
    [0 1]]
   [[1 0]
    [1 0]
    [1 1]]
   [[1 1 1]
    [0 1 0]]])

(def +nb-colors+ 11)

(defn piece-in-game? [{:keys [PIECE X Y]}]
  (and PIECE X Y) )

(defn make-block-pile [X Y]
  (vec (repeat X (vec (repeat Y -1)))))

(defn transpose [MATRIX]
  (apply mapv vector MATRIX))

(defn flip [MATRIX]
  (vec (reverse MATRIX)))

(defn rand-piece []
  (transpose (rand-nth +pieces+)))

(defn complete? [ROW]
  (not-any? #{-1} ROW) )

(defn with-new-piece [GAME]
  (let [PIECE (rand-piece)]
    (assoc GAME
           :X (- 5 (quot (count PIECE) 2))
           :Y 0
           :PIECE PIECE
           :COLOR (rand-int +nb-colors+))))

(defn new-game []
  (with-new-piece
    {:SCORE 0
     :BLOCK-PILE (make-block-pile 10 20)}))

(defn valid-game? [GAME]
  (when (nil? GAME) (throw "nil GAME."))
  (if (piece-in-game? GAME)
    (let [{:keys [X Y PIECE BLOCK-PILE]} GAME]
      (every? #{-1}
              (for [I (range (count PIECE))
                    J (range (count (first PIECE)))
                    :when (pos? (get-in PIECE [I J]))
                    :let [MATRIX-X (+ X I)
                          MATRIX-Y (+ Y J) ]]
                (get-in BLOCK-PILE [MATRIX-X MATRIX-Y]) )))
     true ))


(defn with-completed-rows [{:as GAME :keys [BLOCK-PILE SCORE]}]
  (let [REMAINING-ROWS (remove complete? (transpose BLOCK-PILE))
        CC             (- 20 (count REMAINING-ROWS))
        NEW-ROWS       (repeat CC (vec (repeat 10 -1)))
        NEW-SCORE      (+ (inc SCORE) (* 10 CC CC))]
    (assoc GAME :SCORE NEW-SCORE
                :BLOCK-PILE (transpose (concat NEW-ROWS REMAINING-ROWS)) )))

(defn collect-piece [BLOCK-PILE [X Y COLOR]]
  (assoc-in BLOCK-PILE [X Y] COLOR) )

(defn push-piece [{:as GAME :keys [PIECE COLOR X Y BLOCK-PILE]}]
  (if (piece-in-game? GAME)
    (let [PIECE-WIDTH   (count PIECE)
          PIECE-HEIGHT  (count (first PIECE))]
      (assoc GAME :PIECE nil :X nil :Y nil
                  :BLOCK-PILE  (reduce collect-piece
                                       BLOCK-PILE
                                       (for [I (range PIECE-WIDTH)
                                             J (range PIECE-HEIGHT)
                                             :when (pos? (get-in PIECE [I J]))]
                                         [(+ X I) (+ Y J) COLOR] ))))
    GAME ))


(defn landed [GAME]
  (let [S1 (with-completed-rows (push-piece GAME))
        S2 (with-new-piece S1) ]
    (if (valid-game? S2)
      S2
      (assoc S1 :DONE true) )))

(defn move-down-unchecked [GAME]
  (update-in GAME [:Y] inc) )

;;; ===== game commands

(def +tetris-WorldDef+
  {:valid?-Fn valid-game?
   :Transition {:gravity {:ActionFn              #(let [G (move-down-unchecked %)] (if (valid-game? G) G (landed G)))
                          :eligible?-Fn          piece-in-game?
                          :Action-always-valid?  true }
                ; todo
                }})

(def +tetris-WorldEngine+ (ow/<WorldDef_WorldEngine> +tetris-WorldDef+))

(def gravity (get-in +tetris-WorldEngine+ [:TransitionFn :gravity]))


(defn move-left [GAME]
  (when (nil? GAME) (throw "nil GAME."))
  (if (piece-in-game? GAME)
    (let [NEW-GAME (update-in GAME [:X] dec)]
      (if (valid-game? NEW-GAME)
        NEW-GAME
        GAME ))
    GAME ))

(defn move-right [GAME]
  (when (nil? GAME) (throw "nil GAME."))
  (if (piece-in-game? GAME)
    (let [NEW-GAME (update-in GAME [:X] inc)]
      (if (valid-game? NEW-GAME)
        NEW-GAME
        GAME ))
    GAME ))

(defn rotate [GAME]
  (when (nil? GAME) (throw "nil GAME."))
  (if (piece-in-game? GAME)
    (let [NEW-GAME (update-in GAME [:PIECE] (comp transpose flip))]
      (if (valid-game? NEW-GAME)
        NEW-GAME
        GAME ))
    GAME ))

(defn drop-to-ground [GAME]
  (when (nil? GAME) (throw "nil GAME."))
  (if (piece-in-game? GAME)
    (landed (last (take-while valid-game? (iterate move-down-unchecked GAME))))
    GAME ))












