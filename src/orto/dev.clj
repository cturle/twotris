(ns orto.dev)


; (def-code 'toto '(+ 3 4))
(defn def-code [NAME VALUE]
  `(def ~NAME ~VALUE) )
