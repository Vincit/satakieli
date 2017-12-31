# Satakieli i18n library
[![Clojars Project](https://img.shields.io/clojars/v/vincit/satakieli.svg)](https://clojars.org/vincit/satakieli)
[![Build Status](https://travis-ci.org/Vincit/satakieli.svg?branch=master)](https://travis-ci.org/Vincit/satakieli)
 

<img src="https://upload.wikimedia.org/wikipedia/commons/9/98/Thrush_nightingale2_by_Daniel_Bastaja.jpg" height="250">

(image: [Wikipedia](https://fi.wikipedia.org/wiki/Satakieli))

> A well a everybody's heard about the bird
  B-b-b bird, bird, bird, b-bird's the word
  A well a bird, bird, bird, the bird is the word
  A well a bird, bird, bird, well the bird is the word
  A well a bird, bird, bird, b-bird's the word...

\- The Trashmen

## Overview

This project aims to provide unified Clojure((Script)) interface for ICU MessageFormat. This library wraps [icu4j](http://userguide.icu-project.org/formatparse/messages) when using Clojure and [messageformat.js](https://messageformat.github.io/) when using ClojuresScript.

This library supports both run time compilation of messageformat strings and precompilation when using ClojureScript. Precompilation saves about 50kB of bundle size and speeds up execution. 
## Usage

```clj
(require '[satakieli.compile :as c])
(require '[satakieli.format :as f])

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

(f/translate translations ["fi" "hello"])
=> "Terve!"
(f/translate translations ["en" "candy"] {:count 1})
=> "I have one candy"
(f/translate translations ["en" "candy"] {:count 0})
=> "I have no candies :("
(f/translate translations ["en" "time"] {:now (new java.util.Date)})
=> "Current time is 8:57:33 AM"
```

### Precompile formats using messageformat.js (requires node.js)

Install messageformat.js using node.
```bash
npm install --save-dev messageformat
```
Define translations in json files named \<locale\>.json ie. en.json (see examples/i18n).

Frontend:
```clj
(ns satakieli.example.translations
  (:require-macros [satakieli.messageformat.pre-compile :as pc]))
  
(pc/deformats translations "examples/i18n")
```
Backend:
```clj
(require '[satakieli.messageformat.load :as pc])

(pc/deformats translations "examples/i18n")
```

Then use translations like above.

Messageformat syntax:
http://userguide.icu-project.org/formatparse/messages

### Hot reload for translation json files using figwheel

See dev/user.clj how to create custom watcher for figwheel.

## License

Copyright © 2017 Vincit Oy

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.

## messageformat.js license

Copyright 2012-2016 Alex Sexton, Eemeli Aro, and Contributors

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
