(ns todomvc-quiescent.render
  (:require [cljs.core.async :as a]
            [quiescent :as q :include-macros true]
            [quiescent.dom :as d])
  (:require-macros [cljs.core.async.macros :as am]))

(q/defcomponent Header
  "The page's header, which includes the primary input"
  [[_ input-channel]]
  (d/header {:id "header"}
            (d/h1 {} "todos")
            (d/input {:id "new-todo"
                      :placeholder "What needs to be done?"
                      :onChange (fn [evt]
                                  (let [v (.-value (.-target evt))]
                                    (am/go (a/>! input-channel v))))
                      :autoFocus true})))

(q/defcomponent NavItem
  "A navigation button"
  [[current-nav label href]]
  (d/li {} (d/a {:className (when current-nav "selected")
                 :href href}
                label)))

(q/defcomponent Filters
  "Buttons to filter the list"
  [nav]
  (d/ul {:id "filters"}
        (NavItem [(= :all nav) "All" "#/"])
        (NavItem [(= :active nav) "Active" "#/active"])
        (NavItem [(= :completed nav) "Completed" "#/completed"])))

(q/defcomponent Footer
  "The footer at the bottom of the list"
  [app]
  (let [completed (count (filter :completed (:items app)))
        left (- (count (:items app)) completed)]
    (d/footer {:id "footer"}
              (d/span {:id "todo-count"}
                      (d/strong {} (str left " items left")))
              (Filters (:navigation app))
              (d/button {:id "clear-completed"}
                        (str "Clear completed (" completed ")")))))

(q/defcomponent Item
  "An item in the todo list"
  [item]
  (let [done (:completed item)]
    (d/li {:className (when done "completed")}
          (d/div {:className "view"}
                 (d/input {:className "toggle"
                           :type "checkbox"
                           :checked (when done true)})
                 (d/label {} (:text item))
                 (d/button {:className "destroy"}))
          (d/input {:className "edit"
                    :value (:text item)}))))

(q/defcomponent TodoList
  "The primary todo list"
  [app]
  (apply d/ul {:id "todo-list"}
         (map #(Item %) (:items app))))

(q/defcomponent App
  "The root of the application"
  [[app channels]]
  (d/div {}
         (d/div {} (:current-input app))
         (Header [nil (:input channels)])
         (d/section {:id "main"}
                    (d/input {:id "toggle-all"
                              :type "checkbox"
                              :checked true})
                    (d/label {:htmlFor "toggle-all"}
                             "Mark all as complete")
                    (TodoList app))
         (Footer app)))

(defn render
  "Render the given application. Takes a map of input channels as its
  second argument."
  [app channels]
  (let [root-element (.getElementById js/document "todoapp")]
    (.requestAnimationFrame js/window
                            (fn []
                              (q/render (App [app channels]) root-element)))))


