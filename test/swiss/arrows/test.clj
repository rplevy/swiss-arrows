(ns swiss.arrows.test
  (:require [clojure.string :as str]
            [swiss.arrows :refer :all]
            [clojure.test :refer :all]))

(deftest diamond
  (testing "the diamond wand"
    (is (= (-<> (first [1]))
           1))
    (is (= (-<> 0
                (* <> 5)
                (vector 1 2 <> 3 4))
           [1 2 0 3 4]))
    (is (= (-<> [1 2 3]
                (concat [-1 0] <> [4 5]
                        (-<> 10
                             [7 8 9 <> 11 12]
                             (cons 6 <>))))
           (range -1 13))))

  (testing "vector"
    (is (= (-<> 10 [1 2 3 <> 4 5])
           [1 2 3 10 4 5]))
    (is (vector? (-<> 10 [1 2 3 <> 4 5])))
    (is (= (-<> 0 [<>])
           [0]))
    (is (= (-<> (+ 2 (* 2 3))
                [6 7 <> 9 10]
                (vector <>))
           [[6 7 8 9 10]]))
    (is (= (-<> 10 [1 2 'a <> 4 5])
           [1 2 'a 10 4 5]))
    (is (= (-<> 0 (vector 1 2 3))
           [0 1 2 3]))
    (is (= (-<>> 0 (vector 1 2 3))
           [1 2 3 0]))
    (is (= (-<> 0 [1 2 3])
           [0 1 2 3]))
    (is (= (-<>> 0 [1 2 3])
           [1 2 3 0])))

  (testing "seqs"
    (is (= (-<> 0 (list 1 2 3))
           '(0 1 2 3)))
    (is (= (-<>> 0 (list 1 2 3))
           '(1 2 3 0))))

  (testing "map"
    (is (= (-<> 'foo {:a <> :b 'bar})
           {:a 'foo :b 'bar}))
    (is (= (-<> :a {<> 'foo :b 'bar})
           {:a 'foo :b 'bar})))

  (testing "symbol = (symbol <>)"
    (is (= (-<> :a (map <> [{:a 1} {:a 2}]) vector)
           (-<> :a (map <> [{:a 1} {:a 2}]) (vector <>))))
    (is (= (-<> {:a 1 :b 2} :a inc (vector 1 <> 3))
           [1 2 3]))
    (is (= (-<> :a
                (map <> [{:a 1} {:a 2}])
                (map (partial + 2) <>)
                reverse)
           [4 3])))

  (testing "exception for more than one diamond"
    (is (thrown? Exception (eval '(-<> 0 [1 <> <>])))))

  (testing "default position behavior"
    (is (= (-<> 0 [1 2 3])
           [0 1 2 3]))
    (is (= (-<>> 0 [1 2 3])
           [1 2 3 0]))
    (is (= (-<> 0 (list 1 2 3))
           '(0 1 2 3)))
    (is (= (-<>> 0 (list 1 2 3))
           (list 1 2 3 0)))
    (is (= (-<>> 4 (conj [1 2 3]))
           [1 2 3 4]))
    (is (= (-<> 4 (cons [1 2 3]))
           [4 1 2 3]))
    (is (= (-<>> 4 (conj [1 2 3]) reverse (map inc <>))
           [5 4 3 2]))
    (is (= (-<> 4 (cons [1 2 3]) reverse (map inc <>))
           [4 3 2 5]))))

(deftest back-arrow
  (testing "back arrow"
    (is (= (<<-
            (let [x 'nonsense])
            (if-not x 'foo)
            (let [more 'blah] more))
           (->>
            (let [more 'blah] more)
            (if-not x 'foo)
            (let [x 'nonsense]))))
    (is (= (<<-
            (let [x 'nonsense])
            (if-not x 'foo)
            (let [more 'blah] more))
           (let [x 'nonsense]
             (if-not x 'foo
                     (let [more 'blah] more)))))
    (is (= (<<-
            (let [x 'nonsense])
            (if-not x 'foo)
            (let [more 'blah] more))
           'blah))))

(deftest furculi
  (testing "furculi"
    (is (= (-< (+ 1 2)
               (list 2)
               (list 3)
               (list 4))
           '[(3 2) (3 3) (3 4)]))
    (is (= (-< (+ 1 2)
               (->> vector (repeat 3))
               (-> (* 2) list)
               (list 4))
           '[([3] [3] [3]) (6) (3 4)]))
    (is (= (-<:p (+ 1 2)
                 (list 2)
                 (list 3)
                 (list 4))
           '[(3 2) (3 3) (3 4)]))
    (is (= (-<< (+ 1 2)
                (list 2 1)
                (list 5 7)
                (list 9 4))
           '[(2 1 3) (5 7 3) (9 4 3)]))
    (is (= (-<<:p (+ 1 2)
                  (list 2 1)
                  (list 5 7)
                  (list 9 4))
           '[(2 1 3) (5 7 3) (9 4 3)]))
    (is (= (-<>< (+ 1 2)
                 (list <> 2 1)
                 (list 5 <> 7)
                 (list 9 4 <>))
           '[(3 2 1) (5 3 7) (9 4 3)]))
    (is (= (-<><:p (+ 1 2)
                   (list <> 2 1)
                   (list 5 <> 7)
                   (list 9 4 <>))
           '[(3 2 1) (5 3 7) (9 4 3)]))
    (is (= (-<>>< (+ 1 2)
                  (list <> 2 1)
                  (list 5 <> 7)
                  (list 9 4 <>)
                  (list 10 11))
           '[(3 2 1) (5 3 7) (9 4 3) (10 11 3)]))
    (is (= (-<>><:p (+ 1 2)
                    (list <> 2 1)
                    (list 5 <> 7)
                    (list 9 4 <>)
                    (list 10 11))
           '[(3 2 1) (5 3 7) (9 4 3) (10 11 3)])))

  (testing "parallel time"
    (let [time (Float.
                (str/replace
                 (with-out-str
                   (time (doall
                          (-<<:p
                           "<3 Discordia"
                           (do (Thread/sleep 1000))
                           (do (Thread/sleep 1000))
                           (do (Thread/sleep 1000))))))
                 #"[^\d\.]" ""))]
      (is (< 1000 time 1005))))

  (testing "sequential time"
    (let [time (Float.
                (str/replace
                 (with-out-str
                   (time (doall
                          (-<<
                           "<5 Eris"
                           (do (Thread/sleep 1000))
                           (do (Thread/sleep 1000))
                           (do (Thread/sleep 1000))))))
                 #"[^\d\.]" ""))]
      (is (< 3000 time 3005)))))

(deftest maybe-arrows
  (testing "null-safe swiss arrows"
    (is (nil? (some-<> "abc"
                       (if (string? "adf") nil <>)
                       (str <> " + more"))))
    (is (= (some-<> "abc"
                    (if (string? "adf") "some" <>)
                    (str <> " + more"))
           "some + more"))
    (is (nil? (some-<>> "abc"
                        (if (string? "adf") nil)
                        (str <> "+ more"))))
    (is (= (some-<>> "abc"
                     (if (string? "adf") "some")
                     (str <> "+ more"))
           "some+ more"))))

(deftest applicative
  (testing "applicative arrows"
    (is (= (apply->> [[1 2] [3 4]] concat +)
           10))
    (is (= (apply->> [[1 2] [3 4]] (concat [5 6]))
           [5 6 1 2 3 4]))
    (is (= (apply->> [[1 2] [3 4]] (concat [5 6]) (+))
           21))
    (is (= (apply-> [[1 2] [3 4]] concat +)
           10))
    (is (= (apply-> [1 2 3 4] (concat [[5 6]]))
           [1 2 3 4 5 6]))
    (is (= (apply-> [1 2 3 4] (concat [[5 6]]) (+))
           21))))

(defn wostr* [f]            ; [<results of f>, <side effects to *out* from f>]
  [(f) (with-out-str (f))])
(defmacro wostr [& body]    ; syntactic sugar on wostr*
  `(wostr* (fn [] ~@body)))

(deftest non-updating
  (testing "non-updating arrows"
    (with-out-str
      (is (= (wostr (-!> {:foo "bar"} :foo prn))
             [{:foo "bar"}
              "\"bar\"\n"]))
      (is (= (wostr (-!>> {:foo "bar"} :foo (prn "foo")))
             [{:foo "bar"}
              "\"foo\" \"bar\"\n"]))
      (is (= (wostr (-!<> {:foo "bar"} :foo (prn "got" <> "here")))
             [{:foo "bar"}
              "\"got\" \"bar\" \"here\"\n"]))
      (is (= (wostr (-!<>> {:foo "bar"} :foo (prn "got" "here")))
             [{:foo "bar"}
              "\"got\" \"here\" \"bar\"\n"]))
      (is (= (wostr (-> {:foo "bar"}
                        (assoc :baz ["quux" "you"])
                        (-!> :baz second (prn "got here"))
                        (-!>> :baz second (prn "got here"))
                        (-!<> :baz second (prn "got" <> "here"))
                        (assoc :bar "foo")))
             [{:foo "bar"
               :baz ["quux" "you"]
               :bar "foo"}
              (str "\"you\" \"got here\"\n"
                   "\"got here\" \"you\"\n"
                   "\"got\" \"you\" \"here\"\n")])))))
