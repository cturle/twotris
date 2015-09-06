(ns orto.world-test
  (:require [orto.world :refer :all]
            [clojure.pprint :refer [pprint]] ))


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

; (pprint DEF2)















