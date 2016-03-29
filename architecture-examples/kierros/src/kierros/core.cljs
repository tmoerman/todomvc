(ns kierros.core
  "Cycle-flavoured core functions"
  (:require [cljs.core.async :as a :refer [<! chan]]
            [plumbing.core :refer [map-vals]]))

(defn make-sink-proxies
  "Accepts a map of drivers. Returns a map of the equivalent of Rx.ReplaySubject instance
  for each key in the driver map."
  [driver-map]
  (->> driver-map
       keys
       (map (fn [name] [name (chan)]))
       (into {})))

(defn call-drivers
  "Accepts a map of drivers and a map of sink proxies (channels).
  Returns a map of sources."
  [driver-map sink-proxy-map]
  (->> driver-map
       (map (fn [[name driver]]
              (let [sink-proxy (sink-proxy-map name)
                    source     (driver sink-proxy name)]
                [name source])))
       (into {})))

(defn replicate-many
  ""
  [sink-map sink-proxy-map]
  ;; TODO connect sinks to proxies ~ pipe or something like that
  )

(defn run
  "Cycle.run equivalent. Accepts a main function and a map of drivers."
  [main driver-map]
  (let [sink-proxy-map (make-sink-proxies driver-map)
        source-map     (call-drivers driver-map sink-proxy-map)
        sink-map       (main source-map)
        _              (replicate-many sink-map sink-proxy-map)]
    {:sources source-map
     :sinks   sink-map}))
