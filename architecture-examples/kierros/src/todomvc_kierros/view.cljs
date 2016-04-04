(ns todomvc-kierros.view
  (:require [cljs.core.async :as a :refer [>! chan pipe]]
            [quiescent.core :as q :include-macros true]
            [sablono.core :as s :refer-macros [html]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn enter-key? [evt] (= 13 (.-keyCode evt)))

(q/defcomponent Header
  "Header component, contains the title and the todo input."
  [state {:keys [add-item] :as intent-chans}]
  (html [:header {:id "header"}
         [:h1 {} "Kierros-todos"]
         [:input {:id          "new-todo"
                  :placeholder "your next todo"
                  :autoFocus   true
                  :onKeyDown   (fn [evt]
                                 (when (enter-key? evt)
                                   (let [v (-> evt .-target .-value)]
                                     (go
                                       (>! add-item v))
                                     (-> evt .-target .-value (set! "")))))}]]))

(q/defcomponent Root
  [state intent-chans]
  (html [:div {:id "main"}
         (Header state intent-chans)
         [:ul {:id "todo-list"}
          (->> (:items state)
               (map (fn [i] [:li {} [:label {} (:text i)]])))]]))

(defn view
  "Returns a stream of view trees, represented as a core.async channel."
  [intent-chans states-chan]
  (let [views-chan (->> (fn [state] (Root state intent-chans)) ; fn
                        (map)                                  ; xf
                        (chan 10)                              ; ch
                        (pipe states-chan))]
    views-chan))
