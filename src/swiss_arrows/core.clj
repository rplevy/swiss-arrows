(ns swiss-arrows.core)

(defmacro -<>
  "the 'diamond wand': pass a needle through variably positioned holes
   the <> hole form is not recursive, it only works at the top level.
   also, it works with hash literals, vectors, and quoted forms"
  ([x] x)
  ([x form]
     (let [[process-result form] (if (= 'quote (first form))
                                   [(fn [r] `(quote ~r)) (first (next form))]
                                   [identity             form])
           [process-result form] (if (map? form)
                                   [(comp process-result
                                          (partial apply hash-map))
                                    (apply concat form)]
                                   [process-result       form])
           process-result (if (vector? form)
                            (comp process-result vec)
                            process-result)
           [pre _ post] (partition-by (partial = '<>) form)]
       (process-result (concat pre [x] post))))
  ([x form & forms]
     `(-<> (-<> ~x ~form) ~@forms)))

(defmacro <<-
  "the 'back-arrow': suggested by Stephen Compall in response to a certain
   recently announced Clojure library"
  [& forms]
  `(->> ~@(reverse forms)))

#_(defmacro -<
    "'the divining rod': branch one result into multiple flows" \
    [])

#_(defmacro -<<
    "'the double divining rod': analog of ->> for divining rod"
    [])

#_(defmacro -<><
    "'the diamond fishing rod': analog of -<> for divining rod"
    [])

#_(defmacro -<:p
    "parallel divining rod"
    [])

#_(defmacro -<<:p
    "parallel divining rod, double style"
    [])

#_(defmacro -<><:p
    "parallel diamond fishing rod"
    [])
