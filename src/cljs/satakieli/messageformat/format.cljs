(ns satakieli.messageformat.format)


(defn msg-format [format args]
  (when (fn? format)
    (format (clj->js args))))