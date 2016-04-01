(ns todomvc-kierros.view
  (:require [cljs.core.async :as a :refer [>! chan pipe]]
            [quiescent.core :as q]
            [quiescent.dom :as d])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn enter-key? [evt] (= 13 (.-keyCode evt)))

(q/defcomponent
  Header
  [state add-item-chan]
  (d/header {:id "header"}
            (d/h1 {} "todos")
            (d/input {:id          "new-todo"
                      :placeholder "your next todo"
                      :onKeyDown   (fn [evt]
                                     (when (enter-key? evt)
                                       (let [v (-> evt .-target .-value)]
                                         (go
                                           (>! add-item-chan v))
                                         (-> evt .-target .-value (set! "")))))
                      :autoFocus true})))

(q/defcomponent
  State-display
  [state]
  (println (:items state))
  (d/input {:id "state"
            :placeholder (str state)
            :on-change (fn [evt] (println (str "changed:" state)))
               }))

(q/defcomponent
  Root
  [{:keys [navigate
           add-item
           drop-item
           clear-completed
           toggle-completed
           start-edit
           end-edit] :as intent-chans}
   state]
  (do
    (println "component called")
    (d/div {:id      "main"
            :checked true}
           (Header state add-item)
           (apply d/ul {:id      "todo-list"
                        :checked true} (->> state
                                            :items
                                            (map #(d/li {} (d/label {} (:text %))))
                                            )))))

(defn vtree [intent-chans state]
  (let [result (Root intent-chans state)]
    (do
      (println "vtree called")
      result)))

(defn view
  "Returns a stream of view trees, represented as a core.async channel."
  [intent-chans states-chan]
  (let [views-chan (->> (fn [state]
                          (let [view (vtree intent-chans state)]
                            {:view view
                             :state state})) ; fn
                        (map)                ; xf
                        (chan 10)            ; ch
                        (pipe states-chan))]
    views-chan))
