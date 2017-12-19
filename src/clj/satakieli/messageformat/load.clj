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

(defn load-translations [directory]
  (let [root          (io/file directory)
        root-segments (path-segments root)]
    (->> (file-seq root)
         (filter #(.isFile %))
         (filter #(= "json" (FilenameUtils/getExtension (str %))))
         (map (partial load-file* root-segments))
         (reduce #(apply assoc-in %1 %2) {}))))

(defmacro deformats [sym directory & kw-opts]
  (let [{:keys [keywords?]} (apply hash-map kw-opts)
        translations (cond-> (load-translations directory)
                             keywords?
                             (walk/keywordize-keys))]
    `(def ~sym (c/compile-translations ~translations))))