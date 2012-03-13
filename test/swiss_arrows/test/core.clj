(ns swiss-arrows.test.core
  (:use [swiss-arrows.core]
        [midje.sweet]))

(facts

 (-<> (first [1])) => 1
 
 (-<> 0
      (* <> 5)
      (vector 1 2 <> 3 4))
 => [1 2 0 3 4]

 (-<> [1 2 3]
      (concat [-1 0] <> [4 5]
              (-<> 10
                   [7 8 9 <> 11 12]
                   (cons 6 <>))))
 => (range -1 12)
 
 (-<> (next [1 2 3]) (first)) => (throws AssertionError)

 (-<> (next [1 2 3]) first) => (throws AssertionError)

 (-<> 'foo {:a <> :b 'bar}) => {:a 'foo :b 'bar})
