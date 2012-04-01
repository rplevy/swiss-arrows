(ns swiss-arrows.core)

(defmacro -<>
  "the 'diamond wand': pass a needle through variably positioned holes"
  ([x] x)
  ([x form]
     (let [form (if (map? form) (cons 'hash-map (apply concat form)) form)
           [pre _ post] (partition-by (partial = '<>) form)
           result (concat pre [x] post)]
       (if (vector? form) (vec result) result)))
  ([x form & forms]
     `(-<> (-<> ~x ~form) ~@forms)))


#_(defmacro <<- []
  "the 'double-back-arrow': "
  )

  ;; TODO: macro for spliting into multiple streams


;; TODO: <<-


