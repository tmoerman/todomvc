(ns todomvc-kierros.core
  "Entry namespace for Kierros TODO-MVC app."
  (:require [kierros.core :as cycle]
            [todomvc-kierros.intent :as i]
            [todomvc-kierros.model  :as m]
            [todomvc-kierros.view   :as v]
            [kierros.quiescent-DOM-driver :as dom]
            [kierros.local-storage-driver :as lst]))

(enable-console-print!)

(defonce c (atom 0))

(defn on-js-reload [] (do
                        (print "on-js-reload")
                        (swap! c inc)))

(defn todos-cycle-main
  "Cycle main."
  [& args]
  (let [init-state   (m/init-state)
        intent-chans (i/intents)
        states-chan  (m/model init-state intent-chans)
        views-chan   (v/view intent-chans states-chan)
        pickled-chan nil] ;; TODO serialize the states to be submitted to the storage driver
    {:DOM     views-chan
     :STORAGE pickled-chan}))

(cycle/run
  todos-cycle-main
  {:DOM     (dom/make-DOM-driver "todoapp")
   :STORAGE lst/storage-driver})
