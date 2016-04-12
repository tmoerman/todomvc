(ns todomvc-kierros.model
  (:require [cljs-uuid-utils :refer [uuid-string make-random-uuid]]
            [cljs.core.async :as a :refer [<! chan to-chan pipe]]
            [kierros.model :refer [scan-to-states]]
            [clojure.string :refer [blank?]]))

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
  (if (not (blank? text))
    (update-in state [:items] (fn [items] (-> items
                                              (conj (new-item text)))))
    state))

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
  [_ state]
  (println state)
  (update-in state [:items] (fn [items]
                              (->> items
                                   (remove (fn [item] (-> item :completed true?)))))))

(defn toggle [item] (assoc item :completed (not (:completed item))))

(defn toggle-completed
  ""
  [id state]
  (update-in state [:items] (fn [items]
                              (->> items
                                   (modify id toggle)))))

(defn toggle-all
  ""
  [_ state]
  (update-in state [:items] (fn [items]
                              (->> items
                                   (map toggle)))))

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
   :toggle-all       toggle-all
   :start-edit       start-edit
   :end-edit         end-edit})

(def default-state
  "Initial application state."
  {:filter :all
   :items [(new-item "get milk")
           (-> (new-item "pay taxes")
               (assoc :completed true))]})

(defn model
  [init-state-chan intent-chans]
  (scan-to-states init-state-chan intent-chans intent-handlers))
