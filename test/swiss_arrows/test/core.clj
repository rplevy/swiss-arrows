(ns swiss-arrows.test.core
  (:use [swiss-arrows.core]
        [midje.sweet]))

(facts "the diamond wand"

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
 
 (-<> 'foo {:a <> :b 'bar}) => {:a 'foo :b 'bar})
 
#_(facts "the double-back-arrow"
   
   (<<-
    (let [x nonsense])
    (if early-term thing)
    (let [more blah])
    '[more inner forms…]))