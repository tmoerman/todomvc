(ns todomvc-kierros.model
  (:require [cljs-uuid-utils :refer [uuid-string make-random-uuid]]
            [kierros.util :refer [scan]]))

;; TODO implement state manipulation with Specter.

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

(defn modify
  [id update-fn items]
  (->> items
       (map (fn [item] (if (= id (:id item))
                         (update-fn item) item)))))

(defn toggle-completed
  [id state]
  (update-in state [:items] (fn [items]
                              (->> items
                                   (modify id (fn [item] (assoc item
                                                           :completed (not (:completed item)))))))))

(defn start-edit
  [id state]
  (update-in state [:items] (fn [items]
                              (->> items
                                   (modify id (fn [item] (assoc item
                                                           :editing true)))))))


(defn end-edit
  [[id text] state]
  (update-in state [:items] (fn [items]
                              (->> items
                                   (modify id (fn [item] (assoc item
                                                           :editing false
                                                           :text text)))))))

(defn fresh
  "Returns a new, empty application state."
  []
  {:filter :all
   :items []})
