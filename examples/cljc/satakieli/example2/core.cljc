(ns satakieli.example2.core
  (:require [satakieli.compile :as c]
            [satakieli.format :as f]))


(def translations
  (c/compile-translations
    {"fi" {"hello" "Terve!"
           "name"  "Minun nimi on {name}"
           "time"  "Kello on {now, time}"
           "candy" "Minulla {count, plural, =0 {ei ole karkkeja :(} =1 {on yksi karkki} other {on # karkkia}}"
           "date"  "Tänään on {now, date}"}
     "en" {"hello" "Hello!"
           "name"  "My name is {name}"
           "time"  "Current time is {now, time}"
           "candy" "I have {count, plural, =0 {no candies :(} =1 {one candy} other {# candies}}"
           "date"  "Today is {now, date}"}}))

(defn babble! []
  (let [args {:name  "Turkka"
              :now   #?(:clj  (new java.util.Date)
                        :cljs (new js/Date))
              :count (rand-int 3)}]
    (doall
      (for [locale (-> translations keys)
            key    (-> translations first second keys)]
        (println (f/translate translations [locale key] args))))))

(defn say-hello! [lang]
  (println (f/translate translations [lang "hello"])))

(f/translate translations ["fi" "hello"])
(f/translate translations ["en" "candy"] {:count 1})
(f/translate translations ["en" "candy"] {:count 0})
(f/translate translations ["en" "time"] {:now (new java.util.Date)})

(comment
  (babble!)
  (say-hello! "fi"))