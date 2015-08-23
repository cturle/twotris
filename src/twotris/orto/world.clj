(ns twotris.orto.world)


(declare transition-fn)


; todo
(defn <WorldDef_WorldEngine> [WD]
  (let []
    {:transition-Fn-by-name (into {} name*Fn-voo)} ))


; transifion-fn : <Transition*Fn_Fn>
(defn transition-fn
  [TRANSITION VALID?-FN]
  (let [{:keys ELIGIBLE?-FN ACTION-FN} TRANSITION]
    (fn [CURRENT]
      (when (nil? CURRENT) (throw "CURRENT is nil"))
      (if (ELIGIBLE?-FN CURRENT)
        (let [NEXT (ACTION-FN CURRENT)]
          (if (VALID?-FN NEXT)
            NEXT
            CURRENT ))
        CURRENT ))))
