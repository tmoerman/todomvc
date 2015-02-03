(defproject todomvc-quiescent "0.1.0"
  :plugins [[lein-cljsbuild "1.0.4"]
            [lein-ring "0.8.10"]]
  :dependencies [[org.clojure/clojure "1.7.0-alpha5"]
                 [org.clojure/clojurescript "0.0-2760"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [quiescent/quiescent "0.2.0-SNAPSHOT"]
                 [org.clojars.franks42/cljs-uuid-utils "0.1.3"]
                 [ring "1.2.1"]]
  :ring {:handler todomvc-quiescent.serve/handler}
  ;; Note: it is not idiomatic to compile to, or serve from, the
  ;; project root but TodoMVC wants the index.html there, so we'll
  ;; adjust
  :clean-targets [:target-path :compile-path "generated" "main.js"]
  :cljsbuild {:builds
              {:dev {:source-paths ["src"]
                     :compiler
                     {:output-dir "generated"
                      :output-to "generated/main-dev.js"
                      :optimizations :none
                      :source-map true}}
               :prod {:source-paths ["src"]
                      :compiler
                      {:output-to "main.js"
                       :optimizations :advanced
                       :pretty-print false}}}})

