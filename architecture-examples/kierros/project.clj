(defproject kierros "0.1.0-SNAPSHOT"

  :description "Kierros: Cycle-flavoured ClojureScript with Quiescent"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.8.34"]
                 [org.clojure/core.async "0.2.374" :exclusions [org.clojure/tools.reader]]
                 [prismatic/plumbing "0.5.2"]
                 [quiescent "0.3.1"]
                 [org.clojars.franks42/cljs-uuid-utils "0.1.3"]]

  :plugins [[lein-figwheel "0.5.2"]
            [lein-cljsbuild "1.1.3" :exclusions [[org.clojure/clojure]]]]

  :hooks [leiningen.cljsbuild]

  ;; Note: it is not idiomatic to compile to, or serve from, the
  ;; project root but TodoMVC wants the index.html there, so we'll
  ;; adjust (dixit Quiescent)

  :clean-targets ["resources/public/js/compiled" "target"]

  :cljsbuild {:builds {:dev {:source-paths ["src"]
                             :figwheel {:on-jsload "kierros.core/on-js-reload"}
                             :compiler {:main todomvc-kierros.core
                                        :asset-path "js/compiled/out"
                                        :output-dir "resources/public/js/compiled/out"
                                        :output-to  "resources/public/js/compiled/kierros_main.js"
                                        :optimizations :none
                                        :source-map true}}

                       :prod {:source-paths ["src"]
                              :compiler {:output-to "main.js"
                                         :source-map "main.js.map"
                                         :optimizations :advanced
                                         :pretty-print true}}

                       :test {:source-paths ["src" "test"]
                              :compiler {:output-to "resources/private/js/compiled/unit-test.js"
                                         :optimizations :whitespace
                                         :pretty-print true}}}

              :test-commands {"unit" ["phantomjs"
                                      "resources/private/js/compiled/unit-test.js"]}}

  :profiles {:dev {:dependencies [[com.cemerick/piggieback "0.2.1"]
                                  [org.clojure/tools.nrepl "0.2.10"]
                                  [figwheel-sidecar        "0.5.1"]]
                   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}}}

  :figwheel {:nrepl-port 7888})
