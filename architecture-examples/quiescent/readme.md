# Quiescent TodoMVC Example

> Quiescent is a lightweight ClojureScript library building on ReactJS.

> _[Quiescent on Github](http://github.com/levand/quiescent)_

## Implementation

This example application emphasizes handling an application's state,
in its entirety, as pure immutable data. Updates to the state are
mediated by Clojure's
[core.async](https://github.com/clojure/core.async) channels.

The top-level namespace, `todomvc-quiescent`, creates the application
and wires together all the parts.

The `todomvc-quiescent.render` namespace is solely responsible for
displaying the application. It is comprised mostly of Quiescent
component definitions, as well as a `request-render` function that can
be used to request a re-render of the application on the next
animation frame by calling Quiescent's `render` function at the
appropriate time.

When an event handler is triggered, the relevant data is immediately
placed onto a `core.async` channel. There is one channel for each type
of event the application cares about; this list is enumerated in the
`todomvc-quiescent` namespace, which also sets up consumers for each
channel. When a new value is recieved, it uses the appropriate
function in the `todomvc-quiescent.data` namespace to calculate the
new application state. Receipt and processing of a value via a channel
is also the signal for the application to render.

The functions in `todomvc-quiescent.data` are all pure functions that
take a state and a value and return a new state. As such, they can be
used directly with ClojureScript's `swap!` function.

The `todomvc-quiescent.store` namespace is responsible for concerns
around saving and loading data from HTML5 local storage.

## Running

The compiled JavaScript for the application is distributed in the
GitHub repository. To view the application locally, just open
`index.html` in the browser.

To compile the ClojureScript yourself, run `lein cljsbuild once dev`
(for dev-mode compilation) or `lein cljsbuild once prod` for fully
optimized, minified production code. The dev version can be viewed by
visiting `index-dev.html`. Note that the dev-mode build artifacts are
not checked in to git.

The Clojure project also includes a small embedded server. To view the
project running on localhost, run `lein ring server` and then visit
http://localhost:3000/architecture-examples/quiescent/ . Note that the
trailing slash is required.

## Credit

This TodoMVC application was created by [Luke VanderHart](http://github.com/levand).
