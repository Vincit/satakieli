(ns satakieli.format
  (:require [satakieli.messageformat.format :as msgf]
            [clojure.walk :as walk]
            [clojure.string :as str]))

(defn- format-path-elem [el]
  (cond
    (keyword? el) (name el)
    :default (str el)))

(defn translate
  "Translates path in compiled translation map using messageformat with given arguments
   returns translation or path joined as string when translation is not found"
  ([compiled path args]
   (or (-> (get-in compiled path)
           (msgf/msg-format (walk/stringify-keys args)))
       (str/join "." (map format-path-elem path))))
  ([compiled path]
   (translate compiled path {})))
