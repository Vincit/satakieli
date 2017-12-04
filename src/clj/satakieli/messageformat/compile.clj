(ns satakieli.messageformat.compile
  (:import (java.util Locale)
           (com.ibm.icu.text MessageFormat)))

(def ^:dynamic *customize* identity)

(defn- locale-from-str [locale]
  (Locale/forLanguageTag locale))

(defn compile-format [locale format]
  (*customize*
    (new MessageFormat format (locale-from-str locale))))