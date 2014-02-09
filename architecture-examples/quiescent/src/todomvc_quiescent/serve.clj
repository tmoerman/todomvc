(ns todomvc-quiescent.serve
  "Server for the todomvc app. Not necessary if demoing client-side
   pieces only."
  (:require [ring.middleware.file :as file]
            [ring.middleware.file-info :as fi]))

(def handler
  (-> (fn [_] nil)
      (resource/wrap-file ".")
      (fi/wrap-file-info)))
