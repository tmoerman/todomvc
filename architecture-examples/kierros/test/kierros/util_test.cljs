(ns kierros.util-test
  "Unit tests for the scan transducer."
  (:require [kierros.util :refer [scan]]
            [cljs.test :refer-macros [deftest is testing run-tests]]))

(def amend-state (fn [state f] (f state)))

(deftest sequence-scan-empty
  (let [result (->> []
                    (sequence (scan amend-state)))]
    (is (= result []))))

(deftest sequence-scan-singleton
  (let [result (->> [{:key 1}]
                    (sequence (scan amend-state)))]
    (is (= result [{:key 1}]))))

(deftest sequence-scan-one-amendment
  (let [result (->> [{:key 1}
                     #(assoc % :foo 2)]
                    (sequence (scan amend-state)))]
    (is (= result [{:key 1} {:key 1 :foo 2}]))))

(deftest sequence-scan-two-amendments
  (let [result (->> [{:key 1}
                     #(assoc % :foo 2)
                     #(assoc % :key "one")]
                    (sequence (scan amend-state)))]
    (is (= result [{:key 1} {:key 1 :foo 2} {:key "one" :foo 2}]))))

(run-tests)

