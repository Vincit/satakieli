(ns satakieli.messageformat.load
  (:require [clojure.walk :as walk]
            [clojure.java.io :as io]
            [cheshire.core :as json]
            [satakieli.compile :as c])
  (:import (org.apache.commons.io FilenameUtils)))

(defn- drop-root-segments [root-segments file-path]
  (loop [rs root-segments
         fp file-path]
    (if (= (first rs) (first fp))
      (recur (drop 1 rs) (drop 1 fp))
      fp)))

(defn- path-segments [file]
  (->> file
       .toPath
       .iterator
       iterator-seq
       (map str)
       (map #(FilenameUtils/removeExtension %))))

(defn- load-file* [root-segments file]
  [(drop-root-segments root-segments (path-segments file))
   (json/decode (slurp file))])

(defn merge-deep [m1 m2]
  (merge-with (fn [v1 v2]
                (if (and (map? v1) (map? v2))
                  (merge-deep v1 v2)
                  v2
                  )) m1 m2))

(defn merge-translations [result [path translations]]
  (update-in result path #(merge-deep % translations)))

(defn load-translations [directory]
  (let [root (io/file directory)
        root-segments (path-segments root)]
    (->> (file-seq root)
         (filter #(.isFile %))
         (filter #(= "json" (FilenameUtils/getExtension (str %))))
         (map (partial load-file* root-segments))
         (reduce merge-translations {}))))

(defmacro deformats
  "Compiles messageformat strings from directory dir using messageformat.js
   opts: keywords? "
  [sym directory & kw-opts]
  (let [{:keys [keywords?]} (apply hash-map kw-opts)
        translations (cond-> (load-translations directory)
                             keywords?
                             (walk/keywordize-keys))]
    `(def ~sym (c/compile-translations ~translations))))
