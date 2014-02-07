(ns todomvc-quiescent.data
  (:require [cljs.core.async :as a]
            [cljs-uuid-utils :as uuid])
  (:require-macros [cljs.core.async.macros :as am]))

(defn item
  "Construct a new item"
  [text]
  {:id (uuid/make-random-uuid)
   :text text})

(defn init
  "Loads and returns the initial application state"
  []
  {:navigation :all
   :items [(item "Hello, world!")
           (item "Another item.")]})


