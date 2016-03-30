(ns kierros.util
  (:require [cljs.core.async :as a]))

(defn scan
  "Returns a transducer that implements scan (cfr. Scala) or reductions (cfr. Clojure) semantics."
  [f]
  (fn [xf]
    (let [state (volatile! ::none)] ;; pattern: state scoped around function definition
      (fn
        ([] (xf))
        ([result] (xf result))
        ([result input]
         (let [prev @state
               next (if (= prev ::none)
                      input
                      (f prev input))]
           (vreset! state next)
           (xf result next)))))))

