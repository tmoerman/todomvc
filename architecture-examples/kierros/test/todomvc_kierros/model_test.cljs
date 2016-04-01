(ns todomvc-kierros.model-test
  (:require [todomvc-kierros.model :refer [model]]
            [todomvc-kierros.intent :refer [make-intent-chan]]
            [cljs.core.async :as a :refer [<! >! close! chan to-chan]]
            [cljs.test :refer-macros [deftest is testing run-tests async]])
  (:require-macros [cljs.core.async.macros :refer [go]]))



(deftest model-async
  (async done
    (let [init-state {}
          intent-chans {:foo (make-intent-chan)
                        :gee (make-intent-chan)
                        :bar (make-intent-chan)}
          intent-handlers {:foo (fn [e state] (update-in state [:foo] #(conj % e)))
                           :bar (fn [e state] (update-in state [:bar] #(conj % e)))}
          states-chan (model init-state intent-chans intent-handlers)
          r (a/into [] states-chan)]
      (go
        (-> intent-chans :foo (>! :a))
        (-> intent-chans :foo (>! :b))
        (-> intent-chans :gee (>! :c))
        (-> intent-chans :bar (>! :d))

        (->> intent-chans vals (map close!) dorun)

        (is (= (-> r <! last) {:foo [:b :a] :bar [:d]})))
      (done))))
