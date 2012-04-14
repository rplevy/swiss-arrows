(ns swiss-arrows.core
  (:require [clojure.core.incubator :as incubator]))

(defmacro -<>
  "the 'diamond wand': pass a needle through variably positioned holes
   the <> hole form is not recursive, it only works at the top level.
   also, it works with hash literals, vectors"
  ([x] x)
  ([x form]
     (let [[process-result form]
           (cond (map? form)    [(partial apply hash-map)  (apply concat form)]
                 (vector? form) [vec                       form]
                 (symbol? form) [identity                  (list form '<>)]
                 :otherwise     [identity                  form])]
       (when (not= 1 (count (filter (partial = '<>) form)))
         (throw (Exception. "One diamond per form is required.")))
       (process-result (replace {'<> x} form))))
  ([x form & forms]
     `(-<> (-<> ~x ~form) ~@forms)))

(defmacro <<-
  "the 'back-arrow': suggested by Stephen Compall in response to a certain
   recently announced Clojure library"
  [& forms]
  `(->> ~@(reverse forms)))


(defmacro furcula*
  "would-be private, sugar-free basis of public API"
  [operator parallel? form branches]
  (let [base-form-result (gensym)
        branches (vec branches)]
    `(let [~base-form-result ~form]
       (map ~(if parallel? deref identity)
            ~(cons
              'vector
              (let [branch-forms (for [branch branches]
                                   `(~operator ~base-form-result ~branch))]
                (if parallel?
                  (map (fn [branch-form]
                         `(future ~branch-form)) branch-forms)
                  branch-forms)))))))

(defmacro -<
  "'the furcula': branch one result into multiple flows"
  [form & branches]
  `(furcula* -> nil ~form ~branches))

(defmacro -<:p
  "parallel furcula"
  [form & branches]
  `(furcula* -> :parallel ~form ~branches))

(defmacro -<<
  "'the trystero furcula': analog of ->> for furcula"
  [form & branches]
  `(furcula* ->> nil ~form ~branches))

(defmacro -<<:p
  "parallel trystero furcula"
  [form & branches]
  `(furcula* ->> :parallel ~form ~branches))

(defmacro -<><
  "'the diamond fishing rod': analog of -<> for furcula"
  [form & branches]
  `(furcula* -<> nil ~form ~branches))

(defmacro -<><:p
  "parallel diamond fishing rod"
  [form & branches]
  `(furcula* -<> :parallel ~form ~branches))

(defmacro nilsafe-arrow [docstring non-nilsafe nilsafe]
  (#'incubator/defnilsafe &form &env docstring non-nilsafe nilsafe))

(nilsafe-arrow "the nullsafe version of -<>"
  -<> -?<>)
