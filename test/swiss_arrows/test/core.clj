(ns swiss-arrows.test.core
  (:use [swiss-arrows.core]
        [midje.sweet]))

(facts
 "the diamond wand"

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
 => (range -1 13)

 ;; vector 
 (-<> 10 [1 2 3 <> 4 5])
 => [1 2 3 10 4 5]

 (-<> 10 [1 2 3 <> 4 5])
 => vector?

 ;; quoted vector
 (-<> 10 '[1 2 a <> 4 5])
 => [1 2 'a 10 4 5]

 ;; quoted list
 (-<> 10 '(1 2 a <> 4 5))
 => '(1 2 a 10 4 5)
 
 ;; map
 (-<> 'foo {:a <> :b 'bar})
 => {:a 'foo :b 'bar}

 ;; quoted map
 (-<> foo '{:a a :b <>})
 => {:a 'a :b 'foo})
 
(facts
 "back-arrow"
 
 (<<-
  (let [x 'nonsense])
  (if-not x 'foo)
  (let [more 'blah] more))
 =>
 (->>
  (let [more 'blah] more)
  (if-not x 'foo)
  (let [x 'nonsense]))

 
 (<<-
  (let [x 'nonsense])
  (if-not x 'foo)
  (let [more 'blah] more))
 =>
 (let [x 'nonsense]
   (if-not x 'foo
           (let [more 'blah] more)))

 (<<-
  (let [x 'nonsense])
  (if-not x 'foo)
  (let [more 'blah] more))
 =>
 'blah)


#_(facts
 "divining rods"

 (-< (+ 1 2)
     (list 2)
     (list 3)
     (list 4))
 =>
 '[(3 2) (3 3) (3 4)]

 (-<< (+ 1 2)
      (list 2 1)
      (list 5 7)
      (list 9 4))
 =>
 '[(2 1 3) (5 7 3) (9 4 3)]

 (-<>< (+ 1 2)
       (list <> 2 1)
       (list 5 <> 7)
       (list 9 4 <>))
 =>
 '[(3 2 1) (5 3 7) (9 4 3)]

 (-<:p (+ 1 2)
       (list 2)
       (list 3)
       (list 4))
 =>
 '[(3 2) (3 3) (3 4)]

 (-<<:p (+ 1 2)
        (list 2 1)
        (list 5 7)
        (list 9 4))
 =>
 '[(2 1 3) (5 7 3) (9 4 3)]

 (-<><:p (+ 1 2)
         (list <> 2 1)
         (list 5 <> 7)
         (list 9 4 <>))
 =>
 '[(3 2 1) (5 3 7) (9 4 3)])
