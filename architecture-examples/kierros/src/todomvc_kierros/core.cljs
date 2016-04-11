(ns todomvc-kierros.core
  "Entry namespace for Kierros TODO-MVC app."
  (:require [kierros.core :as cycle]
            [cljs.core.async :as a :refer [mult tap chan sliding-buffer]]
            [todomvc-kierros.intent :as i]
            [todomvc-kierros.model  :as m]
            [todomvc-kierros.view   :as v]
            [kierros.quiescent-DOM-driver :as dom]
            [kierros.local-storage-driver :as lst]))

(enable-console-print!)

(defn on-js-reload [] (print "on-js-reload"))

(defn tap-buf [n mult]
  (let [c (chan (sliding-buffer n))]
    (tap mult c)))

(defn todos-cycle-main
  "Cycle main."
  [{dom-source     :DOM
    storage-source :STORAGE}]
  (let [intent-chans (i/intents)
        states-chan  (m/model storage-source intent-chans)
        states-mult  (mult states-chan)
        view-states-chan   (tap-buf 10 states-mult)
        pickle-states-chan (tap-buf 10 states-mult)
        views-chan   (v/view view-states-chan intent-chans)]
    {:DOM     views-chan
     :STORAGE pickle-states-chan}))

(defn run
  "Application main entry point"
  []
  (cycle/run
    todos-cycle-main
    {:DOM     (dom/make-DOM-driver "todoapp")
     :STORAGE (lst/make-storage-driver "todoapp" m/default-state)}))
