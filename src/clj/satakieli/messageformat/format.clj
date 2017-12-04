(ns satakieli.messageformat.format
  (:import (com.ibm.icu.text MessageFormat)))

(defn msg-format [format args]
  (when (and (instance? MessageFormat format) (map? args))
    (let [sb (new StringBuffer)]
      (str (.format format args sb nil)))))
