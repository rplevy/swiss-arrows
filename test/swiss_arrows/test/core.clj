(ns swiss-arrows.test.core
  (:require [clojure.string :as str]
            [swiss-arrows.core :refer :all]
            [midje.sweet :refer :all]))

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

 (-<> 0 [<>])
 => [0]

 (-<> (+ 2 (* 2 3))
      [6 7 <> 9 10]
      (vector <>))
 => [[6 7 8 9 10]]

 (-<> 10 [1 2 'a <> 4 5])
 => [1 2 'a 10 4 5]

 (-<> 0 (vector 1 2 3)) => [0 1 2 3]
 (-<>> 0 (vector 1 2 3)) => [1 2 3 0]

 (-<> 0 [1 2 3]) => [0 1 2 3]
 (-<>> 0 [1 2 3]) => [1 2 3 0]

 ;; seqs
 (-<> 0 (list 1 2 3)) => '(0 1 2 3)
 (-<>> 0 (list 1 2 3)) => '(1 2 3 0)

 ;; map
 (-<> 'foo {:a <> :b 'bar})
 => {:a 'foo :b 'bar}

 (-<> :a {<> 'foo :b 'bar})
 => {:a 'foo :b 'bar}

 ;; symbol = (symbol <>)

 (-<> :a (map <> [{:a 1} {:a 2}]) vector)
 => (-<> :a (map <> [{:a 1} {:a 2}]) (vector <>))

 (-<> {:a 1 :b 2} :a inc (vector 1 <> 3))
 => [1 2 3]

 (-<> :a
      (map <> [{:a 1} {:a 2}])
      (map (partial + 2) <>)
      reverse)
 => [4 3]

 (eval '(-<> 0 [1 <> <>])) => (throws Exception #"more than one"))

(facts
 "about default position behavior"

 (-<> 0 [1 2 3]) => [0 1 2 3]

 (-<>> 0 [1 2 3]) => [1 2 3 0]

 (-<> 0 (list 1 2 3)) => '(0 1 2 3)

 (-<>> 0 (list 1 2 3)) => (list 1 2 3 0)

 (-<>> 4 (conj [1 2 3])) => [1 2 3 4]

 (-<> 4 (cons [1 2 3])) => [4 1 2 3]

 (-<>> 4 (conj [1 2 3]) reverse (map inc <>)) => [5 4 3 2]

 (-<> 4 (cons [1 2 3]) reverse (map inc <>)) => [4 3 2 5])

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


(facts
 "furculi"

 (-< (+ 1 2)
     (list 2)
     (list 3)
     (list 4))
 =>
 '[(3 2) (3 3) (3 4)]

 (-< (+ 1 2)
     (->> vector (repeat 3))
     (-> (* 2) list)
     (list 4))
 =>
 '[([3] [3] [3]) (6) (3 4)]

 (-<:p (+ 1 2)
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

 (-<<:p (+ 1 2)
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

 (-<><:p (+ 1 2)
         (list <> 2 1)
         (list 5 <> 7)
         (list 9 4 <>))
 =>
 '[(3 2 1) (5 3 7) (9 4 3)]

 ;; compare time of parallel to sequential

 ;; parallel

 (Float.
  (str/replace
   (with-out-str
     (time (doall
            (-<<:p
             "<3 Discordia"
             (do (Thread/sleep 1000))
             (do (Thread/sleep 1000))
             (do (Thread/sleep 1000))))))
   #"[^\d\.]" ""))
 => (roughly 1000 1005)

 ;; sequential

 (Float.
  (str/replace
   (with-out-str
     (time (doall
            (-<<
             "<5 Eris"
             (do (Thread/sleep 1000))
             (do (Thread/sleep 1000))
             (do (Thread/sleep 1000))))))
   #"[^\d\.]" ""))
 => (roughly 3000 3005))

(facts "about null-safe swiss arrows"

  (-?<> "abc"
        (if (string? "adf") nil <>)
        (str <> " + more"))
  => nil)


(facts
 "about non-updating arrows"
 (fact
  (-!> {:foo "bar"} :foo prn)
  => {:foo "bar"}
  (provided (prn "bar") => anything :times 1))

 (fact
  (-!>> {:foo "bar"} :foo (prn "foo"))
  => {:foo "bar"}
  (provided (prn "foo" "bar") => anything :times 1))

 (fact
  (-!<> {:foo "bar"} :foo (prn "got" <> "here"))
  => {:foo "bar"}
  (provided (prn "got" "bar" "here") => anything :times 1))

 (fact
  (-> {:foo "bar"}
      (assoc :baz ["quux" "you"])
      (-!> :baz second (prn "got here"))
      (-!>> :baz second (prn "got here"))
      (-!<> :baz second (prn "got" <> "here"))
      (assoc :bar "foo"))
  => {:foo "bar"
      :baz ["quux" "you"]
      :bar "foo"}
  (provided (prn "you" "got here") => anything :times 1)
  (provided (prn "got here" "you") => anything :times 1)
  (provided (prn "got" "you" "here") => anything :times 1)))
