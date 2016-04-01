(ns todomvc-kierros.model-test
  (:require [todomvc-kierros.model :refer [states-chan]]
            [todomvc-kierros.intent :refer [intent-chan]]
            [cljs.core.async :as a :refer [<! >! close! chan to-chan]]
            [cljs.test :refer-macros [deftest is testing run-tests async]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(deftest states-chan-async
  (async done
    (let [init-state {}
          intent-chans {:foo (intent-chan)
                        :gee (intent-chan)
                        :bar (intent-chan)}
          intent-handlers {:foo (fn [e state] (update-in state [:foo] #(conj % e)))
                           :bar (fn [e state] (update-in state [:bar] #(conj % e)))}
          states-chan (states-chan init-state intent-chans intent-handlers)
          r (a/into [] states-chan)]
      (go
        (-> intent-chans :foo (>! :a))
        (-> intent-chans :foo (>! :b))
        (-> intent-chans :gee (>! :c))
        (-> intent-chans :bar (>! :d))
        ; close all intent channels
        (->> intent-chans vals (map close!) dorun)
        ; collect the scan result and inspect latest value
        (is (= (-> r <! last) {:foo [:b :a] :bar [:d]})))
      (done))))
