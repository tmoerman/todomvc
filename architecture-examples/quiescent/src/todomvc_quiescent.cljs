(ns todomvc-quiescent
  (:require [goog.events :as e]
            [goog.History]
            [cljs.core.async :as a]
            [todomvc-quiescent.render :as render]
            [todomvc-quiescent.data :as data])
  (:require-macros [cljs.core.async.macros :as am]))


(defn init-input-listener
  "Set up input listener"
  [app]
  (let [input-ch (-> app :channels :input)]
    (am/go
      (while true
        (let [m (a/<! input-ch)]
          (swap! (:state app) assoc :current-input m))))))

(defn init-event-listeners
  "Set up event listeners"
  [app]
  (init-input-listener app))

(defn init-history
  "Set up Google Closure history management"
  [app]
  (let [h (goog.History.)]
    (.setEnabled h true)
    (e/listen h goog.History.EventType.NAVIGATE
              (fn [e]
                (a/>!! (-> app :channels :nav) (.-token e))
                (.setToken h (.-token e))))))

(defn init-app
  "Return a map containing a fresh application"
  []
  {:state (atom (data/init))
   :channels {:nav (a/chan (a/sliding-buffer 1))
              :input (a/chan (a/sliding-buffer 1))}})

(defn ^:export main
  "Application entry point"
  []
  (let [app (init-app)]
    (init-history app)
    (init-event-listeners app)
    (add-watch (:state app)
               :ui-watcher
               (fn [_ _ _ new-state]
                 (render/render new-state (:channels app))))
    (render/render @(:state app) (:channels app))))
