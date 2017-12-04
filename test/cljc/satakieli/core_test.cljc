(ns satakieli.core-test
  (:require #?(:clj [clojure.test :refer [deftest is testing]]
               :cljs [cljs.test :refer-macros [deftest is testing]])
                    [satakieli.format :as f]
                    [satakieli.compile :as c])
  #?(:clj
     (:import (java.util Date))))

(def translate-map
  {:fi {:hello          "Terve!"
        :born           "Olen syntynyt {birth_date, date}"
        :time           "Kello on {time, time}"
        :person         "Hänellä on {money, number} euroa"
        :elevator-troll "{troll_count, plural, =0 {mikään ei} one {vesihiisi} other {# vesihiisiä}} {troll_count, plural, =0 {sihissyt} other {sihisi}} hississä"}
   :en {:born   "I'am born {birth_date, date}"
        :time   "Clock is {time, time}"
        :person "{gender, select, male {He} female {She} other {apache attack helicopter}} has {money, number} euros"}})


(def birth-date (new #?(:cljs js/Date :clj Date) 481975320000))

(def current-time (new #?(:cljs js/Date :clj Date) 1512381946809))

(deftest translation-test
  (let [compiled (c/compile-translations translate-map)]
    (testing "Basic translation"
      (is (= (f/translate compiled [:fi :hello]) "Terve!")))
    (testing "Missing translation"
      (is (= (f/translate compiled [:en :hello] "en.hello"))))
    (testing "Plural"
      (is (= (f/translate compiled [:fi :elevator-troll] {:troll_count 0})
             "mikään ei sihissyt hississä"))
      (is (= (f/translate compiled [:fi :elevator-troll] {"troll_count" 1})
             "vesihiisi sihisi hississä"))
      (is (= (f/translate compiled [:fi :elevator-troll] {:troll_count 10})
             "10 vesihiisiä sihisi hississä")))
    #?(:clj (testing "Date"
              (is (= "Olen syntynyt 10.4.1985" (f/translate compiled [:fi :born] {:birth_date birth-date})))
              (is (= "I'am born Apr 10, 1985" (f/translate compiled [:en :born] {:birth_date birth-date})))))
    (testing "Time"
      (is (= "Kello on 12.05.46" (f/translate compiled [:fi :time] {:time current-time})))
      (is (= "Clock is 12:05:46 PM" (f/translate compiled [:en :time] {:time current-time}))))
    (testing "Select and number"
      (is (= "Hänellä on 10 euroa" (f/translate compiled [:fi :person] {:money 10 :gender "male"}))))))