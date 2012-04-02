# swiss-arrows

A collection of arrow macros.

## Usage

*The Diamond Wand*

```
(-<> 0
     (* <> 5)
     (vector 1 2 <> 3 4))
 => [1 2 0 3 4]
```

The diamond wand supports literals and quoted forms:

```
 ;; quoted list
 (-<> 10 '(1 2 a <> 4 5)) => '(1 2 a 10 4 5)
 
 ;; map
 (-<> 'foo {:a <> :b 'bar}) => {:a 'foo :b 'bar}

 ;; quoted map
 (-<> foo '{:a a :b <>}) => {:a 'a :b 'foo})
```

*The Back Arrow*

```
 (<<-
  (let [x 'nonsense])
  (if-not x 'foo)
  (let [more 'blah] more)) => 'blah
```

*The Divining Rod*

```
(-< (+ 1 2) (list 2) (list 3) (list 4)) => '[(3 2) (3 3) (3 4)]

;; The Parallel Divining Rod

(-<:p (+ 1 2) (list 2) (list 3) (list 4)) => '[(3 2) (3 3) (3 4)]
```

*The Double Divining Rod*

```
(-<< (+ 1 2) (list 2 1) (list 5 7) (list 9 4)) => '[(2 1 3) (5 7 3) (9 4 3)]

;; Parallel Divining Rod, Double-style

(-<<:p (+ 1 2) (list 2 1) (list 5 7) (list 9 4)) => '[(2 1 3) (5 7 3) (9 4 3)]
```

*The Diamond Fishing Rod*

```
(-<>< (+ 1 2) [<> 2 1] [5 <> 7] [9 4 <>]) => '[(3 2 1) (5 3 7) (9 4 3)]

;; Parallel Diamond Fishing Rod

(-<><:p (+ 1 2) [<> 2 1] [5 <> 7] [9 4 <>]) => '[(3 2 1) (5 3 7) (9 4 3)]
```



See https://github.com/rplevy/swiss-arrows/blob/master/test/swiss_arrows/test/core.clj for more examples.

## License

Credits:

Walter Tetzner, Stephen Compall, and I designed and implemented something similar to the "diamond wand" a couple of years ago.

Stephen Compall suggested the "back-arrow" in a conversation about a recently announced library, as a better solution (TODO: remember the name of that library).

Copyright (C) 2012 Robert P. Levy

Distributed under the Eclipse Public License, the same as Clojure.
