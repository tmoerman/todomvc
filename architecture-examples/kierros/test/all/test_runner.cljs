(ns ^:figwheel-always all.test-runner
  (:require [kierros.async-test]
            [kierros.core-test]
            [kierros.util-test]
            [todomvc-kierros.model-test]
            [cljs.test :refer-macros [run-tests]]))

(run-tests ;'kierros.core-test
           'kierros.async-test
           'kierros.util-test
           'todomvc-kierros.model-test)

