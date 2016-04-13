(ns todomvc-kierros.view
  (:require [cljs.core.async :as a :refer [>! chan pipe]]
            [quiescent.core :as q :include-macros true :refer-macros [defcomponent]]
            [sablono.core :as s :refer-macros [html]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn enter-key? [evt] (= 13 (.-keyCode evt)))

(defcomponent Header
  "The page's header, which includes the primary input"
  [state {:keys [add-item] :as intent-chans}]
  (html [:header {:id "header"}
         [:h1 {} "Kierros-todos"]
         [:input {:id          "new-todo"
                  :placeholder "whaddayawannado?"
                  :autoFocus   true
                  :onKeyDown   (fn [evt]
                                 (when (enter-key? evt)
                                   (let [v (-> evt .-target .-value)]
                                     (go
                                       (>! add-item v))
                                     (-> evt .-target .-value (set! "")))))}]]))

(defcomponent FocusingInput
  :on-render (fn [node item _] (when (:editing item) (.focus node)))
  [item {:keys [end-edit]}]
  (html [:input {:className     "edit"
                 :defaultValue  (:text item)
                 :onKeyDown     (fn [evt] (when (enter-key? evt) (-> evt .-target .blur)))
                 :onDoubleClick (fn [evt] (-> evt .-target .blur))
                 :onBlur        (fn [evt]
                                  (let [v (-> evt .-target .-value)]
                                    (go (>! end-edit [(:id item) v]))))}]))


(defn class-name [classes] (->> classes (remove nil?) (interpose " ") (apply str)))

(defn hidden? [item filter] (or (and (= filter :active) (:completed item))
                                (and (= filter :completed) (not (:completed item)))))

(defcomponent Todo-item
  :keyfn (fn [[_ item]] (:id item)) ;; don't put the react key in attribute map!
  [[state item] {:keys [toggle-completed start-edit drop-item] :as intent-chans}]
  (let [done   (-> item :completed boolean)
        filter (-> state :filter)
        hidden (hidden? item filter)]
    (html [:li {:className (class-name #{(when done "completed")
                                         (when hidden "hidden")
                                         (when (:editing item) "editing")})}
           [:div {:className "view"}
            [:input {:className "toggle"
                     :type      "checkbox"
                     :checked   done
                     :readOnly  true
                     :onClick   (fn [_] (go (>! toggle-completed (:id item))))}]
            [:label {:onDoubleClick (fn [_] (go (>! start-edit (:id item))))}
             (:text item)]
            [:button {:className "destroy"
                      :onClick   (fn [_] (go (>! drop-item (:id item))))}]]
           (FocusingInput item intent-chans)])))

(defcomponent Todo-list
  [state intent-chans]
  (html [:ul {:id "todo-list"}
         (for [item (:items state)]
           (Todo-item [state item] intent-chans))]))

(defcomponent FilterItem
  [selected? label href]
  (html [:li {} [:a {:className (when selected? "selected")
                     :href      href}
                 label]]))

(defcomponent Filters
  [{filter :filter} _]
  (html [:ul {:id "filters" }
         (FilterItem (= :all filter) "All" "#/")
         (FilterItem (= :active filter) "Active" "#/active")
         (FilterItem (= :completed filter) "Completed" "#/completed")]))

(defn count-label [rest] (str rest " item" (when (< 1 rest) "s") " left"))

(defcomponent Footer
  [state {:keys [clear-completed] :as intent-chans}]
  (let [completed (->> state :items (filter :completed) count)
        rest      (- (-> state :items count) completed)]
    (html [:footer {:id "footer"}
           [:span {:id "todo-count"} [:strong {} (count-label rest)]]
           (Filters state)
           (when (pos? completed)
             [:button {:id      "clear-completed"
                       :onClick (fn [_] (go (>! clear-completed :clear)))}
              (str "Clear completed (" completed ")")])])))

(defcomponent Root
  [state {:keys [toggle-all] :as intent-chans}]
  (html [:div {}
         (Header state intent-chans)
         [:section {:id "main"}
          [:input {:id       "toggle-all"
                   :type     "checkbox"
                   :readOnly false
                   :checked  true
                   :onClick  (fn [_] (go (>! toggle-all :toggle)))}]
          [:label {:htmlFor "toggle-all"} "mark all as complete"]
          (Todo-list state intent-chans)]
         (Footer state intent-chans)]))

(defn view
  "Returns a stream of view trees, represented as a core.async channel."
  [states-chan intent-chans]
  (let [views-chan (->> (fn [state] (Root state intent-chans)) ; fn
                        (map)                                  ; xf
                        (chan 10)                              ; ch
                        (pipe states-chan))]
    views-chan))
