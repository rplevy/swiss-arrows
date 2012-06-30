# swiss-arrows

A collection of arrow macros.

![trystero](http://upload.wikimedia.org/wikipedia/en/archive/a/a9/20060119154504!Trystero-small.png)

## Usage

### Installation

http://clojars.org/swiss-arrows

### Getting Started

Clojure 1.4
```
(ns example.core
  (:require [swiss-arrows.core :refer :all]))
```

Clojure 1.3
```
(ns example.core
  (:use [swiss-arrows.core]))
```
 
### Overview

**-<>** The Diamond Wand

**-?<>** The Nil-shortcutting Diamond Wand

**-!>** , **-!>>** , **-!<>** Non-updating Arrows

**<<-** The Back Arrow

**-<** , **-<:p** The Furcula, Parallel Furcula

**-<<** , **-<<:p** The Trystero Furcula, Parallel Trystero Furcula

**-<><** , **-<><:p** The Diamond Fishing Rod, Parallel Diamond Fishing Rod

### A Generalization of the Arrow

*The Diamond Wand* - similar to -> or ->> except that the flow of execution
is passed through specified <> positions in each of the forms.

```clojure
(-<> 0
     (* <> 5)
     (vector 1 2 <> 3 4))
 => [1 2 0 3 4]
```

The diamond wand also supports literals:

```clojure
 ;; map
 (-<> 'foo {:a <> :b 'bar}) => {:a 'foo :b 'bar}

 ;; vector
 (-<> 10 [1 2 3 <> 4 5])    => [1 2 3 10 4 5]
```

Like -> & ->> interpret a symbol x as (x), -<> interprets x as (x <>)

```clojure
 (-<> :a
      (map <> [{:a 1} {:a 2}])
      (map (partial + 2) <>)
      reverse)
 => [4 3]
```

*Nil-shortcutting Diamond Wand*

Contributed by Alex Baranosky.

```clojure
 (-?<> "abc"
       (if (string? "adf") nil <>)
       (str <> " + more"))
 => nil)
```

### Non-updating Arrows (for unobtrusive side-effecting)

It is often expedient, particular for debugging and logging, to stick a side-
effecting form midway in the pipeline of an arrow.  One solution is to define
utility macros such as ["with" and "within"](https://gist.github.com/3021378)
One trade-off here is that having too many anaphoric macros can lead to messy
code, and they don't nest (cf. #( ) reader macro), and so on.

Non-updating arrows offer an adequately elegant solution for inserting action
of this sort in what would otherwise be an inconvenient situation.

```clojure
  (-> {:foo "bar"}
    (assoc :baz ["quux" "you"])
    (-!> :baz second (prn "got here"))
    (-!>> :baz first (prn "got here"))
    (-!<> :baz second (prn "got" <> "here"))
    (assoc :bar "foo"))
  => {:foo "bar"
      :baz ["quux" "you"]
      :bar "foo"}
```

### The Back Arrow

This is simply ->> with its arguments reversed, convenient in some cases.  It
was contributed by Stephen Compall as an alternative to
[egamble/let-else](http://github.com/egamble/let-else).

```clojure
 (<<-
  (let [x 'nonsense])
  (if-not x 'foo)
  (let [more 'blah] more)) => 'blah
```

### Branching Arrows

The following six arrows (three, and their parallel counterparts) are
*branching* arrows, in contrast with the "threading" or "nesting" arrows we 
have seen thus far.  The following example demonstrates how branching and 
nesting arrows can work together to cleanly express a flow of control. Here 
our first branching arrow, The Furcula, passes the result of (+ 1 2) to each 
of the successive forms, which is then nested out horizontally into further 
expressions using traditional arrows.

```clojure
(-< (+ 1 2)
    (->> vector (repeat 3))
    (-> (* 2) list)
    (list 4))                =>    '[([3] [3] [3]) (6) (3 4)]
```


*The Furcula* - a branching arrow using the -> form placement convention. 
Expands to a let performing the initial operation, and then individual 
expressions using it. In the parallel version, the individual expressions are
 evaluated in futures.

```clojure
(-< (+ 1 2) (list 2) (list 3) (list 4)) => '[(3 2) (3 3) (3 4)]

;; The Parallel Furcula

(-<:p (+ 1 2) (list 2) (list 3) (list 4)) => '[(3 2) (3 3) (3 4)]
```

*The Trystero Furcula* - another branching arrow. Same idea as -<, except it
uses the ->> form placement convention.

```clojure
(-<< (+ 1 2) (list 2 1) (list 5 7) (list 9 4)) => '[(2 1 3) (5 7 3) (9 4 3)]

;; Parallel Trystero Furcula

(-<<:p (+ 1 2) (list 2 1) (list 5 7) (list 9 4)) => '[(2 1 3) (5 7 3) (9 4 3)]
```

*The Diamond Fishing Rod* - another branching arrow. Same idea as -< and -<<,
except it uses the -<> form placement convention.

```clojure
(-<>< (+ 1 2) [<> 2 1] [5 <> 7] [9 4 <>]) => '[(3 2 1) (5 3 7) (9 4 3)]

;; Parallel Diamond Fishing Rod

(-<><:p (+ 1 2) [<> 2 1] [5 <> 7] [9 4 <>]) => '[(3 2 1) (5 3 7) (9 4 3)]
```

See https://github.com/rplevy/swiss-arrows/blob/master/test/swiss_arrows/test/core.clj for more examples.

## License

Credits:

Walter Tetzner, Stephen Compall, and I designed and implemented something
similar to the "diamond wand" a couple of years ago.

Thanks to Alex Baranosky, Roman Perepelitsa and Stephen Compall for
constructive feedback and ideas.

Copyright (C) 2012 Robert P. Levy

Distributed under the Eclipse Public License, the same as Clojure.
