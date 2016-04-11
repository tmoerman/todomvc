(ns todomvc-kierros.core
  "Entry namespace for Kierros TODO-MVC app."
  (:require [kierros.core :as cycle]
            [todomvc-kierros.intent :as i]
            [todomvc-kierros.model  :as m]
            [todomvc-kierros.view   :as v]
            [kierros.quiescent-DOM-driver :as dom]
            [kierros.local-storage-driver :as lst]))

(enable-console-print!)

(defn on-js-reload [] (print "on-js-reload"))

(defn todos-cycle-main
  "Cycle main."
  [& args]
  (let [init-state   (m/init-state)
        intent-chans (i/intents)
        states-chan  (m/model init-state intent-chans)
        views-chan   (v/view states-chan intent-chans)
        pickled-chan nil] ;; TODO serialize the states to be submitted to the storage driver
    {:DOM     views-chan
     :STORAGE pickled-chan}))

(defn run
  "Application main entry point"
  []
  (cycle/run
    todos-cycle-main
    {:DOM     (dom/make-DOM-driver "todoapp")
     :STORAGE lst/storage-driver}))
