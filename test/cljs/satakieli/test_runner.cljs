(ns satakieli.test-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [satakieli.core-test]
            [satakieli.precompile-test]))

(doo-tests 'satakieli.core-test 'satakieli.precompile-test)