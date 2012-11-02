(ns swiss-arrows.core)

(defmacro thread-into
  "Inserts x in place of '<>' in form, or in first or last position as indicated
  by posk"
  [form x posk]
  (let [rf (fn [f] (replace {'<> x} f))
        cf (fn [f] (count (filter (partial = '<>) f)))
        c (cond 
            (or (seq? form) (vector? form)) (cf form)
            (map? form) (cf (mapcat concat form))
            :default 0)]    
    (cond
      (< 1 c) (throw (Exception. "No more than one position per form is allowed."))
      (or (symbol? form) (keyword? form)) `(~form ~x)
      (= 0 c) (cond (vector? form) (if (= :first posk) `(cons ~x ~form) `(conj ~form ~x))
                    (seq? form) (if (= :first posk) `(cons ~x ~form) `(concat ~form (list ~x)))
                    :default form)
      (vector? form) (rf form)
      (map? form) (apply hash-map (mapcat rf form))
      (= 1 c) `(~(first form) ~@(rf (next form)))
      :default (cond (= :first posk) `(~(first form) ~x ~@(next form))
                     (= :last posk) `(~(first form) ~@(next form) ~x)))))

(defmacro -<>
  "the 'diamond wand': top-level insertion of x in place of single
   positional '<>' symbol within the threaded form if present, otherwise
   mostly behave as the thread-first macro. Also works with hash literals
   and vectors."
  ([x] x)
  ([x form] `(thread-into ~form ~x :first))
  ([x form & forms] `(-<> (-<> ~x ~form) ~@forms)))

(defmacro -<>>
  "the 'diamond spear': top-level insertion of x in place of single
   positional '<>' symbol within the threaded form if present, otherwise
   mostly behave as the thread-last macro. Also works with hash literals
   and vectors."
  ([x] x)
  ([x form] `(thread-into ~form ~x :last))
  ([x form & forms] `(-<>> (-<>> ~x ~form) ~@forms)))

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

(defmacro ^:private defnilsafe [docstring non-safe-name nil-safe-name]
  `(defmacro ~nil-safe-name ~docstring
     {:arglists '([~'x ~'form] [~'x ~'form ~'& ~'forms])}
     ([x# form#]
        `(let [~'i# ~x#] (when-not (nil? ~'i#) (~'~non-safe-name ~'i# ~form#))))
     ([x# form# & more#]
             `(~'~nil-safe-name (~'~nil-safe-name ~x# ~form#) ~@more#))))

(defnilsafe "the nullsafe version of -<>"
  -<> -?<>)

(defmacro -!>
  "non-updating -> for unobtrusive side-effects"
  [form & forms]
  `(let [x# ~form] (-> x# ~@forms) x#))

(defmacro -!>>
  "non-updating ->> for unobtrusive side-effects"
  [form & forms]
  `(let [x# ~form] (->> x# ~@forms) x#))

(defmacro -!<>
  "non-updating -<> for unobtrusive side-effects"
  [form & forms]
  `(let [x# ~form] (-<> x# ~@forms) x#))
