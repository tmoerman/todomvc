(ns todomvc-quiescent
  (:require [goog.events :as e]
            [goog.History]
            [cljs.core.async :as a]
            [todomvc-quiescent.render :as render]
            [todomvc-quiescent.data :as data]
            [todomvc-quiescent.updates :as updates])
  (:require-macros [cljs.core.async.macros :as am]))

(defn init-history
  "Set up Google Closure history management"
  [app]
  (let [h (goog.History.)]
    (.setEnabled h true)
    (e/listen h goog.History.EventType.NAVIGATE
              (fn [evt]
                (let [ch (-> app :channels :nav)
                      token (.-token evt)]
                  (.setToken h token)
                  (am/go (a/>! ch token)))))))

(defn init-app
  "Return a map containing a fresh application"
  []
  {:state (atom (data/init))
   :channels {:nav (a/chan)
              :submit (a/chan)
              :destroy (a/chan)
              :toggle (a/chan)
              :clear-completed (a/chan)
              :start-edit (a/chan)
              :complete-edit (a/chan)}
   :consumers {:nav data/set-filter
               :submit data/add
               :destroy data/destroy
               :toggle data/toggle
               :clear-completed data/clear-completed
               :start-edit data/start-edit
               :complete-edit data/complete-edit}})

(defn init-updates
  "For every entry in a map of channel identifiers to consumers,
  initiate a channel listener which will update the application state
  using the appropriate function whenever a value is recieved, as
  well as triggering a render."
  [app]
  (doseq [[ch update-fn] (:consumers app)]
    (am/go (while true
             (let [val (a/<! (get (:channels app) ch))
                   new-state (swap! (:state app) update-fn val)]
               (render/render new-state (:channels app)))))))

(defn ^:export main
  "Application entry point"
  []
  (let [app (init-app)]
    (init-history app)
    (init-updates app)
    (render/render @(:state app) (:channels app))))


