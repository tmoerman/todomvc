(ns todomvc-kierros.core
  "Entry namespace for Kierros TODO-MVC app."
  (:require [kierros.core :as cycle]
            [kierros.quiescent-DOM-driver :as dom]))

(enable-console-print!)
(println "Figgin' around like there's no tomorrow!")
(defn on-js-reload [] )

(defn todos-cycle-main
  "Cycle main."
  [])

(cycle/run todos-cycle-main {:DOM dom/create-quiescent-DOM-driver})


