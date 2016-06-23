(ns todomvc-kierros.core
  "Entry namespace for Kierros TODO-MVC app."
  (:require [cljs.core.async :as a :refer [mult tap chan pipe sliding-buffer]]
            [todomvc-kierros.intent :as i]
            [todomvc-kierros.model  :as m]
            [todomvc-kierros.view   :as v]
            [kierros.core :as cycle]
            [kierros.history :as h]
            [kierros.quiescent-dom-driver :as dom]
            [kierros.local-storage-driver :as lst]))

(enable-console-print!)

(defn on-js-reload [] (print "js reloaded" (js/Date.)))

(defonce history-mult (mult (h/init-history)))

(defn todos-cycle-main
  "Cycle main."
  [{saved-state-chan :STORAGE}]
  (let [intent-chans (i/intent)
        _            (a/untap-all history-mult) ; because of Figwheel reloading.
        _            (tap history-mult (:navigate intent-chans))
        states-chan  (m/model saved-state-chan intent-chans)
        states-mult  (mult states-chan)
        view-states-chan   (->> (chan) (tap states-mult))
        pickle-states-chan (->> (sliding-buffer 1) (chan) (tap states-mult))
        views-chan   (v/view view-states-chan intent-chans)]
    {:DOM     views-chan
     :STORAGE pickle-states-chan}))

(cycle/run
  todos-cycle-main
  {:DOM     (dom/make-DOM-driver "todoapp")
   :STORAGE (lst/make-storage-driver "todomvc_kierros" m/default-state)})
