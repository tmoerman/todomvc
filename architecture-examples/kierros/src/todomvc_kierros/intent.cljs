(ns todomvc-kierros.intent
  (:require [cljs.core.async :as a :refer [<! chan dropping-buffer]]))

(def intents
  [:navigate
   :add-item
   :drop-item
   :clear-completed
   :toggle-completed
   :start-edit
   :end-edit])

(defn make-intent-chan [] (chan (dropping-buffer 10)))

(defn intent-chans
  "Returns a map of event channels."
  []
  (->> intents
       (map (fn [key] [key (make-intent-chan)]))
       (into {})))
