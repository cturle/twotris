(ns twotris.orto.world
  (:require [twotris.orto.dev :refer :all]) )


(declare spec-code
         transition-code )


(defmacro defspec [SPEC]
  (spec-code SPEC) )

(defn spec-code [{WORLD :World, FN-vec :Fn}]
  (let [CHECK-PRE? true
        STMTS      (for [{NAME :name, [_ TRANSITION-NAME] :spec} FN-vec]
                     (let [TRANSITION (get-in WORLD [:Transition TRANSITION-NAME])
                           ACTION     (get-in WORLD [:Action (:Action TRANSITION)]) ]
                       (def-code NAME (transition-code CHECK-PRE? (when-not (:always-valid? ACTION) (:valid?-Fn WORLD)) (:eligible?-Fn ACTION) (:Code ACTION))) ))]
    `(let [] ~@STMTS) ))


; (def DEF1 (transition-code false nil nil inc))
; (def F1   (eval DEF1))
; (def DEF2 (transition-code true 'number? 'number? 'inc))
; (def F2   (eval DEF2))
; (clojure.pprint/pprint DEF2)
(defn transition-code
  [CHECK-PRE? VALID?-FN ELIGIBLE?-FN ACTION-FN]
  (let [CURRENT               (gensym "CURRENT-")
        CHECK-PRE-STMTS       (when CHECK-PRE? `[(when (nil? ~CURRENT) (throw "CURRENT is nil"))])
        ACTION-STMT           `(~ACTION-FN ~CURRENT)
        ENSURE-VALID-STMT     (if VALID?-FN
                                (let [NEXT (gensym "NEXT-")]
                                  `(let [~NEXT ~ACTION-STMT]
                                     (if (~VALID?-FN ~NEXT)
                                       ~NEXT
                                       ~CURRENT )))
                                ACTION-STMT )
        ENSURE-ELIGIBLE-STMT  (if ELIGIBLE?-FN
                                `(if (~ELIGIBLE?-FN ~CURRENT)
                                   ~ENSURE-VALID-STMT
                                   ~CURRENT )
                                ENSURE-VALID-STMT )
        BODY-STMTS            [ENSURE-ELIGIBLE-STMT] ]
    `(fn [~CURRENT]
        ~@CHECK-PRE-STMTS
        ~@BODY-STMTS )))

(def DEF2 (spec-code
 '{:World  {:valid?-Fn  valid-game?
            :Transition {:gravity         {:Action  :gravity}
                         :move-left       {:Action  :move-left}
                         :move-right      {:Action  :move-right}
                         :rotate          {:Action  :rotate}
                         :drop-to-ground  {:Action  :drop-to-ground}
                         }
            :Action     {:gravity         {:Code           #(let [G (move-down-unchecked %)] (if (valid-game? G) G (landed G)))
                                           :eligible?-Fn   piece-in-game?
                                           :always-valid?  true }
                         :move-left       {:Code           #(update-in % [:X] dec)
                                           :eligible?-Fn   piece-in-game? }
                         :move-right      {:Code           #(update-in % [:X] inc)
                                           :eligible?-Fn   piece-in-game? }
                         :rotate          {:Code           #(update-in % [:PIECE] (comp transpose flip))
                                           :eligible?-Fn   piece-in-game? }
                         :drop-to-ground  {:Code           #(landed (last (take-while valid-game? (iterate move-down-unchecked %))))
                                           :eligible?-Fn   piece-in-game?
                                           :always-valid?  true }
                         }
            }
   :Fn  [{:name gravity,        :spec [:Transition :gravity]}
         {:name move-left,      :spec [:Transition :move-left]}
         {:name move-right,     :spec [:Transition :move-right]}
         {:name rotate,         :spec [:Transition :rotate]}
         {:name drop-to-ground, :spec [:Transition :drop-to-ground]} ]}))
















