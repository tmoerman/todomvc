(ns kierros.util)

(defn scan
  "Returns a transducer that implements scan or reductions semantics."
  [f]
  (fn [xf]
    (let [state (volatile! ::none)]
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

