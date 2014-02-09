(ns todomvc-quiescent.render
  (:require [cljs.core.async :as a]
            [quiescent :as q :include-macros true]
            [quiescent.dom :as d])
  (:require-macros [cljs.core.async.macros :as am]))

(def enter-key 13)

(q/defcomponent Header
  "The page's header, which includes the primary input"
  [_ submit-ch]
  (d/header {:id "header"}
            (d/h1 {} "todos")
            (d/input {:id "new-todo"
                      :placeholder "What needs to be done?"
                      :onKeyDown
                      (fn [evt]
                        (if (= 13 (.-keyCode evt))
                          (let [v (.-value (.-target evt))]
                            (am/go (a/>! submit-ch v))
                            (set! (.-value (.-target evt)) ""))))
                      :autoFocus true})))

(q/defcomponent FilterItem
  "A filtering button"
  [selected? label href]
  (d/li {} (d/a {:className (when selected? "selected")
                 :href href}
                label)))

(q/defcomponent Filters
  "Buttons to filter the list"
  [filter]
  (d/ul {:id "filters"}
        (FilterItem (= :all filter) "All" "#/")
        (FilterItem (= :active filter) "Active" "#/active")
        (FilterItem (= :completed filter) "Completed" "#/completed")))

(q/defcomponent Footer
  "The footer at the bottom of the list"
  [app channels]
  (let [completed (count (filter :completed (:items app)))
        left (- (count (:items app)) completed)]
    (d/footer {:id "footer"}
              (d/span {:id "todo-count"}
                      (d/strong {} (str left " items left")))
              (Filters (:filter app))
              (when (< 0 completed)
                (d/button {:id "clear-completed"
                           :onClick #(am/go (a/>! (:clear-completed channels)
                                                  :clear))}
                          (str "Clear completed (" completed ")"))))))

(defn class-name
  "Convenience function for creating class names from sets. Nils will
  not be included."
  [classes]
  (apply str (interpose " " (map identity classes))))

(defn hidden?
  "Given an item and the current application filter status, return
  true if the item should be hidden."
  [item filter]
  (or (and (= filter :active) (:completed item))
      (and (= filter :completed) (not (:completed item)))))

(q/defcomponent Item
  "An item in the todo list"
  [[item filter] channels]
  (let [done (boolean (:completed item))]
    (d/li {:key (:id item)
           :className (class-name #{(when done "completed")
                                    (when (hidden? item filter) "hidden")
                                    (when (:editing item) "editing")})
           :onDoubleClick (fn [evt]
                            (am/go (a/>! (:start-edit channels)
                                         (:id item))))}
          (d/div {:className "view"}
                 (d/input {:className "toggle"
                           :type "checkbox"
                           :checked done
                           :onClick
                           (fn [_]
                             (am/go (a/>! (:toggle channels)
                                          (:id item))))})
                 (d/label {} (:text item))
                 (d/button {:className "destroy"
                            :onClick
                            (fn [_]
                              (am/go (a/>! (:destroy channels)
                                           (:id item))))}))
          (let [complete-edit (fn [evt]
                                (let [v (.-value (.-target evt))]
                                  (am/go (a/>! (:complete-edit channels)
                                               [(:id item) v]))))]
            (q/on-render
             (d/input {:className "edit"
                       :defaultValue (:text item)
                       :onKeyDown (fn [evt]
                                    (when (= 13 (.-keyCode evt))
                                      (complete-edit evt)))
                       :onBlur complete-edit})
             (fn [node]
               (when (:editing item) (.focus node))))))))

(q/defcomponent TodoList
  "The primary todo list"
  [app channels]
  (apply d/ul {:id "todo-list"} (map #(Item [% (:filter app)] channels)
                                     (:items app))))

(q/defcomponent App
  "The root of the application"
  [app channels]
  (d/div {}
         (Header nil (:submit channels))
         (d/section {:id "main"}
                    (d/input {:id "toggle-all"
                              :type "checkbox"
                              :checked true})
                    (d/label {:htmlFor "toggle-all"}
                             "Mark all as complete")
                    (TodoList app channels))
         (Footer app channels)))

(defn render
  "Render the given state tree. Takes a map of input channels as its
  second argument."
  [app channels]
  (let [root-element (.getElementById js/document "todoapp")]
    (.requestAnimationFrame js/window
                            (fn []
                              (q/render (App app channels) root-element)))))


