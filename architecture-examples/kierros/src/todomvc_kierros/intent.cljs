(ns todomvc-kierros.intent
  (:require [cljs.core.async :as a :refer [<! chan dropping-buffer]]))

(def intent-keys
  [:navigate
   :add-item
   :drop-item
   :clear-completed
   :toggle-completed
   :start-edit
   :end-edit])

(defn intent-chan [] (chan (dropping-buffer 10)))

(defn intents
  "Returns a map of event channels."
  []
  (->> intent-keys
       (map (fn [key] [key (intent-chan)]))
       (into {})))
