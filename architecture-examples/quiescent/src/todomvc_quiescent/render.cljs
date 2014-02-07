(ns todomvc-quiescent.render
  (:require [cljs.core.async :as a]
            [quiescent :as q :include-macros true]
            [quiescent.dom :as d])
  (:require-macros [cljs.core.async.macros :as am]))

(q/defcomponent Header
  "The page's header, which includes the primary input"
  []
  (d/header {:id "header"}
            (d/h1 {} "todos")
            (d/input {:id "new-todo"
                      :placeholder "What needs to be done?"
                      :onChange (fn [evt]
                                  (.log js/console (-> evt .-target .-value)))
                      :autoFocus true})))

(q/defcomponent Filters
  "Buttons to filter the list"
  [nav]
  (d/ul {:id "filters"}
        (d/li {} (d/a {:className (when (= :all nav)
                                    "selected")
                       :href "#/"}
                      "All"))
        (d/li {} (d/a {:className (when (= :active nav)
                                    "selected")
                       :href "#/active"}
                      "Active"))
        (d/li {} (d/a {:className (when (= :completed nav)
                                         "selected")
                       :href "#/completed"}
                      "Completed"))))


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
  [app]
  (d/div {}
         (Header nil)
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
  [app]
  (let [root-element (.getElementById js/document "todoapp")]
    (.requestAnimationFrame js/window
                            (fn []
                              (q/render (App app) root-element)))))


