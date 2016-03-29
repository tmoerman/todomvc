(ns kierros.quiescent-DOM-driver
  "Cycle DOM driver, powered by quiescent."
  (:require [cljs.core.async :as a :refer [<! chan]]))

(defn create-quiescent-DOM-driver
  "Accepts a DOM container as rendering root.
  Returns a DOM driver powered by quiescent."
  [container]
  (fn [vtree$ & args] nil))
