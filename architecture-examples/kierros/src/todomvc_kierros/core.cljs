(ns todomvc-kierros.core
  "Entry namespace for Kierros TODO-MVC app."
  (:require [kierros.core :as cycle]
            [kierros.util :refer [scan]]
            [kierros.quiescent-DOM-driver :as dom]
            [kierros.local-storage-driver :as lst]))

(enable-console-print!)

(defn on-js-reload [] )

(defn todos-cycle-main
  "Cycle main."
  [])

(cycle/run
  todos-cycle-main
  {:DOM     (dom/create-DOM-driver ".todoapp")
   :STORAGE lst/storage-driver})
