(ns todomvc-kierros.model
  (:require [cljs-uuid-utils :refer [uuid-string make-random-uuid]]
            [cljs.core.async :as a :refer [<! chan to-chan pipe]]
            [kierros.util :refer [scan]]
            [kierros.async :refer [chain]]))

;; TODO implement state manipulation with Specter.

(defn navigate [] :todo)

(defn new-item
  "Construct a new item"
  [text]
  {:id (uuid-string (make-random-uuid))
   :text text})

(defn add-item
  "Add specified item to the state."
  [text state]
  (update-in state [:items] (fn [items] (-> items
                                            (conj (new-item text))))))

(defn drop-item
  "Remove item with specified id from the :items in the state."
  [id state]
  (update-in state [:items] (fn [items] (->> items
                                             (remove (fn [item] (= id (item :id))))))))

(defn- modify
  ""
  [id update-fn items]
  (->> items
       (map (fn [item] (if (= id (:id item))
                         (update-fn item) item)))))

(defn clear-completed
  ""
  [state]
  (update-in state [:item] (fn [items]
                             (->> items
                                  (remove (fn [item] (-> item :completed true?)))))))

(defn toggle-completed
  ""
  [id state]
  (update-in state [:items] (fn [items]
                              (->> items
                                   (modify id (fn [item] (assoc item
                                                           :completed (not (:completed item)))))))))

(defn start-edit
  ""
  [id state]
  (update-in state [:items] (fn [items]
                              (->> items
                                   (modify id (fn [item] (assoc item
                                                           :editing true)))))))

(defn end-edit
  ""
  [[id text] state]
  (update-in state [:items] (fn [items]
                              (->> items
                                   (modify id (fn [item] (assoc item
                                                           :editing false
                                                           :text text)))))))

(def intent-handlers
  {:navigate         navigate
   :add-item         add-item
   :drop-item        drop-item
   :clear-completed  clear-completed
   :toggle-completed toggle-completed
   :start-edit       start-edit
   :end-edit         end-edit})

(defn init-state
  "Returns a new, empty application state."
  []
  {:filter :all
   :items [(new-item "first!")]})

(defn scan-to-states
  ; TODO generic enough to factored out to Kierros namespace.
  "Returns a stream of application states, represented as a core.async channel."
  [init-state intent-chans intent-handlers]
  (let [buf-or-n      10
        amend-fn-chan (->> intent-chans
                           (map (fn [[key ch]]
                                  (when-let [intent-handler (key intent-handlers)]
                                    (->> #(partial intent-handler %) ; fn
                                         (map)                       ; xf
                                         (chan buf-or-n)             ; ch
                                         (pipe ch)))))               ; piped
                           (remove nil?) ; only channel with handler
                           (a/merge))
        initial-chan  (to-chan [init-state])
        states-chan   (->> (fn [state f] (f state)) ; fn
                           (scan)                   ; xf
                           (chan buf-or-n)          ; ch
                           (pipe (chain [initial-chan amend-fn-chan])))]
    states-chan))

(defn model
  [init-state intent-chans]
  (scan-to-states init-state intent-chans intent-handlers))
