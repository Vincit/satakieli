(ns satakieli.example.translations
  (:require-macros [satakieli.messageformat.load :as pc]))

(pc/deformats translations "examples/i18n")
