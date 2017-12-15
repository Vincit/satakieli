(ns satakieli.precompile-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [satakieli.format :as sf])
  (:require-macros
    [satakieli.messageformat.pre-compile :as pc]))


(pc/deformats translations "examples/i18n")
(pc/deformats translations-kw "examples/i18n" :keywords? true)

(defn- path [keywords? & ks]
  (if keywords?
    ks
    (map name ks)))

(defn- check-simple [translations keywords?]
  (is (= "Terve!" (sf/translate translations (path keywords? :fi :hello))))
  (is (= "Hello!" (sf/translate translations (path keywords? :en :hello)))))

(defn- check-plural [translations keywords?]
  (is (= "Minulla ei ole karkkeja :(" (sf/translate translations (path keywords? :fi :candy) {:count 0}))))

(deftest precompiled
  (testing "Simple translation"
    (check-simple translations false))
  (testing "Plural"
    (check-plural translations false)))

(deftest precompiled-kw
  (testing "Simple translation kw"
    (check-simple translations-kw true))
  (testing "Plural kw"
    (check-plural translations-kw true)))
