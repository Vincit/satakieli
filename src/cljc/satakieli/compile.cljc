(ns satakieli.compile
  (:require [clojure.walk :as walk]
            [satakieli.messageformat.compile :as compile]))

(defn- coerce-locale [locale]
  (if (keyword? locale)
    (name locale)
    locale))

(defn compile-translations
  ([locale translation-map]
   "Compiles translation-map leaves to messageformat classes / functions using given locale"
   (let [compile-format (partial compile/compile-format (coerce-locale locale))]
     (walk/postwalk
       (fn [form]
         (if (and (vector? form) (string? (second form)))
           (update form 1 compile-format)
           form))
       translation-map)))
  ([translation-map]
   "Compiles translation-map to messageformat classes / functions
    first level is expected to contain locale keys ie. {:fi ... :en ...}"
   (->> translation-map
        (map (fn [[locale v]]
               [locale (compile-translations locale v)]))
        (into {}))))