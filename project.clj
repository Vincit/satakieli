(defproject vincit/satakieli "0.2.1"
  :description "Wrapper around messageformat.js and Java implementation. Provides helper for precompiling messageformat."
  :url "https://github.com/Vincit/satakieli"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}



  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]
                 [org.clojure/core.async "0.3.443"]
                 [com.ibm.icu/icu4j "60.1"]]

  :plugins [[lein-figwheel "0.5.14"]
            [lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]
            [lein-shell "0.5.0"]
            [lein-doo "0.1.8"]]

  :figwheel-watch {:dir          "examples/i18n"
                   :file-pattern "*.json"
                   :touch-file   "examples/cljs/translations.cljs"}

  :source-paths ["src/cljc" "src/clj" "src/cljs" "examples/cljc"]

  :test-paths ["test/cljc"]

  :doo {:build "test"
        :alias {:browsers [:chrome :firefox]
                :headless [:chrome-headless]}}

  :cljsbuild {:builds
              [{:id           "dev"
                :source-paths ["src/cljc" "src/cljs" "examples/cljs" "examples/cljc"]
                ;; The presence of a :figwheel configuration here
                ;; will cause figwheel to inject the figwheel client
                ;; into your build
                :figwheel     {:on-jsload "satakieli.example.core/on-js-reload"}
                :compiler     {:main                 satakieli.example.core
                               :asset-path           "js/compiled/out"
                               :output-to            "examples/public/js/compiled/satakieli.js"
                               :output-dir           "examples/public/js/compiled/out"
                               :source-map-timestamp true
                               ;; To console.log CLJS data-structures make sure you enable devtools in Chrome
                               ;; https://github.com/binaryage/cljs-devtools
                               :preloads             [devtools.preload]}}
               {:id           "min"
                :source-paths ["src/cljc" "src/cljs" "examples/cljs"]
                :compiler     {:main          satakieli.example.core
                               :output-to     "examples/public/js/compiled/satakieli.js"
                               :optimizations :advanced
                               :pretty-print  false}}
               {:id           "test"
                :source-paths ["src/cljc" "src/cljs" "test/cljs" "test/cljc"]
                :compiler     {:output-to     "target/js/testable.js"
                               :output-dir    "target/js/out/testable"
                               :main          satakieli.test-runner
                               :optimizations :none}}]}


  ;; Setting up nREPL for Figwheel and ClojureScript dev
  ;; Please see:
  ;; https://github.com/bhauman/lein-figwheel/wiki/Using-the-Figwheel-REPL-within-NRepl
  :profiles {:dev     {:dependencies  [[binaryage/devtools "0.9.4"]
                                       [figwheel-sidecar "0.5.14"]
                                       [com.cemerick/piggieback "0.2.2"]
                                       [re-frame "0.10.2"]
                                       [reagent "0.8.0-alpha1"]]
                       ;; need to add dev source path here to get user.clj loaded
                       :source-paths  ["src/clj" "src/cljc" "dev"]
                       ;; for CIDER
                       ;; :plugins [[cider/cider-nrepl "0.12.0"]]
                       :repl-options  {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
                       ;; need to add the compliled assets to the :clean-targets
                       :clean-targets ^{:protect false} ["examples/public/js/compiled/"
                                                         :target-path]}
             :uberjar {}})
