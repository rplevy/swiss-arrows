(ns swiss-arrows.core)

(defmacro -<>
  "the 'diamond wand': pass a needle through variably positioned holes
   the <> hole form is not recursive, it only works at the top level.
   also, it works with hash literals, vectors"
  ([x] x)
  ([x form]
     (let [[process-result form]
           (cond (map? form)    [(partial apply hash-map)  (apply concat form)]
                 (vector? form) [(comp process-result vec)  form]
                 :otherwise     [identity                   form])
           [pre _ post] (partition-by (partial = '<>) form)]
       (process-result (concat pre [x] post))))
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
