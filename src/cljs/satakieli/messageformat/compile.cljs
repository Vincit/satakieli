(ns satakieli.messageformat.compile
  (:require [messageformat]))

(def ^:dynamic *customize* identity)

(def ^:private format-for-locale
  (memoize
    (fn [locale]
      (-> (new js/MessageFormat locale)
          *customize*
          (.setIntlSupport true)))))

(defn compile-format [locale format]
  (.compile (format-for-locale locale) format))

