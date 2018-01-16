(ns satakieli.messageformat.pre-compile
  (:require [clojure.java.shell :as sh]))

(defn- wrap-closure [code]
  (str "(function() { var exports = exports || {};"
       code
       "return exports; })();"))

(defn- exec-messageformat [dir]
  (let [{:keys [exit out err] :as result}
        (sh/sh "./node_modules/.bin/messageformat" "-n" "exports" "--enable-intl-support" "true" dir)]
    (if (= exit 0)
      out
      (throw (ex-info "Could not compile formats" result)))))

(defmacro deformats
  "Compiles messageformat strings from directory dir using messageformat.js
   opts: keywords? "
  [sym dir & kwarg-opts]
  (let [{:keys [keywords?]
         :or   {keywords? false}} (apply hash-map kwarg-opts)
        code (-> (exec-messageformat dir) wrap-closure)]
    `(def ~sym (-> (js/eval ~code)
                   (cljs.core/js->clj)
                   (cljs.core/cond->
                     ~keywords?
                     (clojure.walk/keywordize-keys))))))