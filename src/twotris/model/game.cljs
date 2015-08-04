(ns twotris.model.game)

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

(defn make-block-pile [X Y]
  (vec (repeat X (vec (repeat Y -1)))))

(defn transpose [MATRIX]
  (apply mapv vector MATRIX))

(defn flip [MATRIX]
  (vec (reverse MATRIX)))

(defn rand-piece []
  (transpose (rand-nth +pieces+)))

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

(defn valid-game? [{:keys [X Y PIECE BLOCK-PILE DONE]}]
  (every? #{-1}
          (for [I (range (count PIECE))
                J (range (count (first PIECE)))
                :when (pos? (get-in PIECE [I J]))
                :let [MATRIX-X (+ X I)
                      MATRIX-Y (+ Y J)]]
            (get-in BLOCK-PILE [MATRIX-X MATRIX-Y]))))

(defn complete? [ROW]
  (not-any? #{-1} ROW) )

(defn with-completed-rows [{:as GAME :keys [BLOCK-PILE]}]
  (let [REMAINING-ROWS (remove complete? (transpose BLOCK-PILE))
        CC             (- 20 (count REMAINING-ROWS))
        NEW-ROWS       (repeat CC (vec (repeat 10 -1))) ]
    (-> GAME
        (update-in [:SCORE] inc)
        (update-in [:SCORE] + (* 10 CC CC))
        (assoc :BLOCK-PILE (transpose (concat NEW-ROWS REMAINING-ROWS))))))

(defn collect-piece [BLOCK-PILE [X Y COLOR]]
  (assoc-in BLOCK-PILE [X Y] COLOR) )

(defn push-piece [{:as GAME :keys [PIECE COLOR X Y BLOCK-PILE]}]
  (let [PIECE-WIDTH   (count PIECE)
        PIECE-HEIGHT  (count (first PIECE))]
    (assoc GAME :BLOCK-PILE
           (reduce collect-piece BLOCK-PILE
                   (for [I (range PIECE-WIDTH)
                         J (range PIECE-HEIGHT)
                         :when (pos? (get-in PIECE [I J]))]
                     [(+ X I) (+ Y J) COLOR])))))

(defn maybe-done [GAME]
  (if (valid-game? GAME)
    GAME
    (assoc GAME :DONE true)))

(defn landed [GAME]
  (-> GAME
      push-piece
      with-completed-rows
      with-new-piece
      maybe-done))

;(defn maybe-step [GAME F]
;  (let [NEW-GAME (F GAME)]
;    (if (valid-game? NEW-GAME)
;      NEW-GAME
;      GAME )))


(defn move-down-unchecked [GAME]
  (update-in GAME [:Y] inc))

(defn gravity [GAME]
  (let [NEW-GAME  (move-down-unchecked GAME)]
    (if (valid-game? NEW-GAME)
      NEW-GAME
      (landed GAME))))

(defn move-left [GAME]
  (let [NEW-GAME (update-in GAME [:X] dec)]
    (if (valid-game? NEW-GAME)
      NEW-GAME
      GAME )))

(defn move-right [GAME]
  (let [NEW-GAME (update-in GAME [:X] inc)]
    (if (valid-game? NEW-GAME)
      NEW-GAME
      GAME )))

(defn rotate [GAME]
  (let [NEW-GAME (update-in GAME [:PIECE] (comp transpose flip))]
    (if (valid-game? NEW-GAME)
      NEW-GAME
      GAME )))

(defn drop-to-ground [GAME]
  (landed (last (take-while valid-game? (iterate move-down-unchecked GAME)))))




