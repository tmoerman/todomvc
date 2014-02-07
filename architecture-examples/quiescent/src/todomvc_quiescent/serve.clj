(ns todomvc-quiescent.serve
  (:require [ring.middleware.file :as file]
            [ring.middleware.content-type :as ct]
            [ring.middleware.not-modified :as nm]))

(def handler
  (-> (fn [_] nil)
      (file/wrap-file "resources/public/")
      (ct/wrap-content-type)
      (nm/wrap-not-modified)))
