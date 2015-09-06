(ns orto.world
  (:require [orto.dev :as dev]) )


(declare spec-code
         transition-code )


(defmacro defcode
  "Macro which define code from specifications."
  [SPEC]
  (spec-code SPEC) )


; (def DEF1 (transition-code true 'number? 'number? 'inc))
; (clojure.pprint/pprint DEF1)
(defn transition-code
  "return the transition code from its action function."
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

(defn spec-code
  "build world functions code from world specification."
  [{WORLD :World, FN-vec :Fn}]
  (let [CHECK-PRE? true
        STMTS      (for [{NAME :name, [_ TRANSITION-NAME] :spec} FN-vec]
                     (let [TRANSITION (get-in WORLD [:Transition TRANSITION-NAME])
                           ACTION     (get-in WORLD [:Action (:Action TRANSITION)]) ]
                       (dev/def-code NAME (transition-code CHECK-PRE? (when-not (:always-valid? ACTION) (:valid?-Fn WORLD)) (:eligible?-Fn ACTION) (:Code ACTION))) ))]
    `(let [] ~@STMTS) ))



















