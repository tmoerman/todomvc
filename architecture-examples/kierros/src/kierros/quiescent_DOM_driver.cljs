(ns kierros.quiescent-DOM-driver
  "Cycle DOM driver, powered by quiescent."
  (:require [cljs.core.async :refer [<! chan]]
            [quiescent.core :as q :include-macros true])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))

(defn make-DOM-driver
  "Accepts a DOM container as rendering root.
  Returns a DOM driver powered by quiescent.
  A driver takes a channel and returns a channel."
  [container]
  (fn [views-chan & args]
    (go-loop []
             (when-let [view (<! views-chan)]
               (.requestAnimationFrame
                 js/window
                 (fn [] (q/render view (.getElementById js/document container)))))
             (recur))
    (chan))) ; For now not much is going on here. Dom event should be put on this chan.
