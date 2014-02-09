(ns todomvc-quiescent.serve
  "Server for the todomvc app. Not necessary if demoing client-side
   pieces only."
  (:require [ring.middleware.resource :as resource]
            [ring.middleware.file-info :as fi]))

(def handler
  (-> (fn [_] nil)
      (resource/wrap-resource "public")
      (fi/wrap-file-info)))

