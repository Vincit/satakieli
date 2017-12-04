(ns satakieli.example.translations
  (:require-macros [satakieli.messageformat.pre-compile :as pc]))

(pc/deformats translations "examples/i18n")
