(defproject todomvc-quiescent "0.1.0"
  :plugins [[lein-cljsbuild "1.0.3"]
            [lein-ring "0.8.10"]]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2227"]
                 [org.clojure/core.async "0.1.303.0-886421-alpha"]
                 [quiescent/quiescent "0.1.2-SNAPSHOT"]
                 [org.clojars.franks42/cljs-uuid-utils "0.1.3"]
                 [ring "1.2.1"]]
  :ring {:handler todomvc-quiescent.serve/handler}
  ;; Note: it is not idiomatic to compile to, or serve from, the
  ;; project root but TodoMVC wants the index.html there, so we'll
  ;; adjust
  :cljsbuild {:builds
              {:dev {:source-paths ["src"]
                     :compiler
                     {:output-dir "generated"
                      :output-to "generated/main-dev.js"
                      :optimizations :none
                      :source-map true
                      }}
               :prod {:source-paths ["src"]
                      :compiler
                      {:output-to "main.js"
                       :optimizations :advanced
                       :preamble ["react/react.min.js"]
                       :externs ["react/externs/react.js"]
                       :pretty-print false
                       :closure-warnings {:non-standard-jsdoc :off}}}}})

