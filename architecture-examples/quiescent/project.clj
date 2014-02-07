(defproject todomvc-quiescent "0.1.0"
  :plugins [[lein-cljsbuild "1.0.1"]]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2156"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [quiescent/quiescent "0.1.0-SNAPSHOT"]
                 [org.clojars.franks42/cljs-uuid-utils "0.1.3"]]
  :cljsbuild {:builds
              [{:source-paths ["src"]
                :compiler
                {:output-to "resources/public/build/main.js"
                 :output-dir "resources/public/build/work"
                 :optimizations :none
                 :pretty-print true
                 :source-map true}}
               {:source-paths ["src"]
                :compiler
                {:output-to "resources/public/build/main-prod.js"
                 :optimizations :advanced
                 :preamble ["react/react.min.js"]
                 :externs ["react/externs/react.js"]
                 :pretty-print false
                 :closure-warnings {:non-standard-jsdoc :off}}}]})

