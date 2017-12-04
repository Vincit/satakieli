(ns user
  (:require
    [figwheel-sidecar.repl-api :as f]
    [figwheel-sidecar.components.file-system-watcher :as fsw]
    [figwheel-sidecar.system :as fs]
    [com.stuartsierra.component :as component])
  (:import (org.apache.commons.io.filefilter WildcardFileFilter)))

(def TRANSLATIONS-NS "examples/cljs/satakieli/example/translations.cljs")

;custom watcher from https://github.com/ptaoussanis/tower/issues/70
(defn touch [file-path]
  (-> file-path clojure.java.io/file (.setLastModified(System/currentTimeMillis))))

(defn create-notification-handler [file-pattern touch-file]
  (let [f (WildcardFileFilter. (str file-pattern))]
    (fn [_watcher files]
      (when (not-empty (filter #(.accept f nil %) (map str files)))
        (touch touch-file)))))


(defn custom-watcher [figwheel-server]
  (fsw/file-system-watcher {:watcher-name         "Custom Watcher"
                            :watch-paths          ["examples/i18n"]
                            :notification-handler (create-notification-handler
                                                    "*.json"
                                                    TRANSLATIONS-NS)
                            :figwheel-server figwheel-server }))

(def translation-watcher (atom nil))

(defn server-from-repl-api []
  (-> f/*repl-api-system* :figwheel-system :system deref :figwheel-server))


(defn fig-stop
  "Stop the figwheel server and watch based auto-compiler."
  []
  (f/stop-figwheel!)
  (when @translation-watcher
    (do
      (component/stop @translation-watcher)
      (reset! translation-watcher nil))))

(defn fig-start
  "This (re)starts the figwheel server and watch based auto-compiler."
  []
  (fig-stop)
  (f/start-figwheel!)
  (reset! translation-watcher (component/start (custom-watcher (server-from-repl-api))))
  (touch TRANSLATIONS-NS))

(defn cljs-repl
  "Launch a ClojureScript REPL that is connected to your build and host environment."
  []
  (f/cljs-repl))
