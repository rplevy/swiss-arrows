# swiss-arrows

A collection of arrow macros.

![trystero](http://upload.wikimedia.org/wikipedia/en/archive/a/a9/20060119154504!Trystero-small.png)

## Usage

### Installation

http://clojars.org/swiss-arrows

### Getting Started

```
(ns example.core
  (:require [swiss.arrows :refer :all]))
```

### Overview

**-<>** , **-<>>** The Diamond Wand, Diamond Spear

**some-<>** , **some-<>>** The Nil-shortcutting Diamond Wand

**apply->** , **apply->>** Applicative arrows (WIP)

**-!>** , **-!>>** , **-!<>** Non-updating Arrows

**<<-** The Back Arrow

**-<** , **-<:p** The Furcula, Parallel Furcula

**-<<** , **-<<:p** The Trystero Furcula, Parallel Trystero Furcula

**-<><** , **-<><:p** The Diamond Fishing Rod, Parallel Diamond Fishing Rod

**=>** The Pointless Arrow

**+>** The Secret Arrow

### A Generalization of the Arrow

*The Diamond Wand* - similar to -> or ->> except that the flow of execution
is passed through specified <> positions in each of the forms.

```clojure
(-<> 2
     (* <> 5)
     (vector 1 2 <> 3 4))
 => [1 2 10 3 4]
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

*Default Positioning Behavior*

If no <> position marker is found in a form within the Diamond Wand -<>, the
default positioning behavior follows that of the -> macro. Likewise, if no
position is specified in a form within the Diamond Spear -<>>, the default is
has the positioning semantics of ->>.

Some examples:

```clojure
  (-<> 0 [1 2 3]) => [0 1 2 3]

  (-<> 0 (list 1 2 3)) => '(0 1 2 3)

  (-<>> 4 (conj [1 2 3])) => [1 2 3 4]

  (-<> 4 (cons [1 2 3]) reverse (map inc <>)) => [4 3 2 5])
```

*Nil-shortcutting Diamond Wand*

```clojure
 (some-<> "abc"
   (if (string? "adf") nil <>)
   (str <> " + more"))
 => nil
```

### Applicative Arrows (WIP)

```clojure
 (apply->>
   [[1 2] [3 4]]
   (concat [5 6]))        => [5 6 1 2 3 4]

 (apply->> [[1 2] [3 4]]
   (concat [5 6])
   +)                     => 21
```

Based on idea suggested by @rebcabin:

>“Since I noticed that the "nil-shortcutting diamond wand" acts like the Maybe monad, I started getting the feeling that the swiss arrows could be generalized over all monads. Since the archetype of monads is the sequence monad and the mother operator, bind, for sequence is (apply concat (map my-foo your-sequence-monad)) I started to see chaining of apply-concat as a start toward monadic swiss arrows :)

>Also, Wolfram / Mathematica have a host of operators that thread and merge Apply around expressions (see http://reference.wolfram.com/mathematica/ref/Apply.html). Mathematica was designed before monads were formalized in programming languages, but their precursors are all over Mathematica, for instance in the frequent use of Apply. [edit: I should add, contextually, that I am a big admirer of Mathematica just as a programming language, never mind its huge knowledge base of math. I often refer to it for ideas to bring to Clojure and other languages.]

>As for the first argument being special, that didn't seem out-of-step with the other arrows, none of which, for instance, can take an expression with an angle-hole "<>" in the first position, and all of which take expressions-with-angle-holes in every slot except the first, modulo the defaults. The defaults are abundant and require the same kind of mental substitution that apply->> etc. would require, so the overall design has established the precedent of "implicits."”

### Non-updating Arrows (for unobtrusive side-effecting)

It is often expedient, in particular for debugging and logging, to stick a
side-effecting form midway in the pipeline of an arrow.  One solution is a
pair of utility macros ["with" and "within"](https://gist.github.com/3021378)
A caveat of that approach is that having too many anaphoric macros can lead
to messy code, and they don't nest (eg. #( ) reader macro), and so on.

Non-updating arrows offer an adequately elegant alternative solution for
inserting side-action in what would otherwise be a difficult situation.
As a bonus, the arrow-style macros (including the wand-- the <> does not refer
to any sort of binding, and does not act recursively so it is not anaphoric
in the usual sense, if at all) do not rely on symbol capture, and therefore
are arbitrarily nestable.


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

This is simply ->> with its arguments reversed, convenient in some cases.
It was suggested as an alternative to
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

See [the tests](https://github.com/rplevy/swiss-arrows/blob/master/test/swiss/arrows/test.clj) for more examples.

###The Definition Arrows
The following three arrows allow for function definitions to be simplified when relying on the diamond wand.
They also allow for a quick way of enforcing a primarily point free style (hence the name).

*The Pointless Arrow*
Creates a `defn` using `-<>` which is either arity 1, if the first form is a seq, or
arity N if the first form is a function.

```clojure
(=> mathify
    (- 3 <>)
    inc)

==>

(defn
  mathify
  [arg]
  (-<>
    (- 3 <>)
    inc))
```

*The Secret Arrow*
Similar to the Pointless Arrow, but using `defn-`.

```clojure
(+> mathify
    (- 3 <>)
    inc)

==>

(defn-
  mathify
  [arg]
  (-<>
    (- 3 <>)
    inc))
```
*The High Diamond*
Similar to the Pointless Arrow, but using `defmacro`.

```clojure
(<<>> mathify
    (- 3 <>)
    inc)

==>

(defmacro
  mathify
  [arg]
  (-<>
    (- 3 <>)
    inc))
```

## License

Credits:

Walter Tetzner, Stephen Compall, and I designed and implemented something
similar to the "diamond wand" a couple of years ago.

Thanks to Alex Baranosky, Roman Perepelitsa, Paul Dorman, @rebcabin, and Stephen Compall for code contributions and conceptual contributions.

Copyright (C) 2012 Robert P. Levy

Distributed under the Eclipse Public License, the same as Clojure.
