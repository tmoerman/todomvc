(ns todomvc-quiescent.store
  (:require [cljs.reader :as r]))

(defn store
  "Save the given application state in HTML5 localstorage"
  [state]
  (.setItem js/localStorage "todomvc.quiescent" (str state)))

(defn load
  "Load the application state from HTML5 localstorage, nil if it doesn't exist"
  []
  (when-let [s (.getItem js/localStorage "todomvc.quiescent")]
    (r/read-string s)))

(defn init-persistence
  "Given an application, watch it for changes and persist via localstorage"
  [app]
  (add-watch (:state app) :persistence
             (fn [_ _ _ new-state] (store new-state))))
