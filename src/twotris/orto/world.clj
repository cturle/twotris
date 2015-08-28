(ns twotris.orto.world)


(declare transition-fn)


(defn <World_WorldEngine> [W]
  (let [NAME*TRANSITION-mio  (into {} (for [[NAME TRANSITION] (get W :Transition)] [NAME (transition-fn-from-transition TRANSITION (:valid?-Fn W))]))]
    {:TransitionFn NAME*TRANSITION-mio} ))


; <Transition*Fn_Fn>
(defn transition-fn-from-transition
  [{ELIGIBLE?-FN :eligible?-Fn, ACTION-FN :ActionFn} VALID?-FN]
  (transition-fn VALID?-FN ELIGIBLE?-FN ACTION-FN) )

(defn transition-fn
  [VALID?-FN ELIGIBLE?-FN ACTION-FN]
  (fn [CURRENT]
    (when (nil? CURRENT) (throw "CURRENT is nil"))
    (if (ELIGIBLE?-FN CURRENT)
      (let [NEXT (ACTION-FN CURRENT)]
        (if (VALID?-FN NEXT)
          NEXT
          CURRENT ))
      CURRENT )))



