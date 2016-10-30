(ns clj-tparse.core
  (:require [instaparse.core :as insta]
            [clojure.set :refer [map-invert]]))

(declare configure-parser vectormap->rdf-edn)

(def turtle-config-file "resources/rdf-turtle-spec.txt")
(def input-file "resources/example0.ttl")

(def resultset-map (atom {}))

;; TODO convert turtle input to EDN triples
;; - TODO 2 Convert Instaparse output to EDN


(defn re-apply
  [vm]
  (fn [w] (apply vectormap->rdf-edn w)))

(defn print-vm
  [vm]
  (fn [] (println "vm:" vm)))

(def function-map
  {:turtleDoc re-apply
   :triples re-apply})

(defn vectormap->rdf-edn
  ([vm]
   (function-map vm (apply vectormap->rdf-edn vm)))
  ([keyword & rest]
   (function-map keyword (println "kw:" keyword "rest:" rest) rest)))

(defn get-content
  [content-holder]
  (slurp content-holder))

(defn configure-parser
  [config-file]
  (insta/parser config-file))

(def parser-def (insta/parser turtle-config-file))

(defn parse
  [content]
  (insta/parse parser-def (get-content content)))


(defn read-file
  []
  (slurp input-file))

(defn input-line
  [file]
  (doseq [line (clojure.string/split-lines file)]
    (println "Een:" line)))


(defn start!
  []
  (println "no action yet"))

(comment (defn line [n filename]
           (with-open [rdr (io/reader filename)]
             (doall (drop (- n 1) (take n (line-seq rdr))))))

         (def turtle
           (insta/parser
             (slurp turtle-tst-file) :trace true)))


;;;; unfinished REPL generated functions

(defn print-value
  [first]
  (fn [rest] (println "first is:" first "\n" "Rest is:" rest "\n" "rest count is:" (count rest))))

(comment
  (defn vm2rdf-edn
   ([vm]
    (apply vm2rdf-edn vm))
   ([first & rest]
    (if (keyword? first)
      (condp = first
        :turtleDoc ((print-value first) rest)
        :triples ((print-value first) rest)
        :subject ((print-value first) rest)
        :iri ((print-value first) rest)
        :predicateObjectList ((print-value first) rest)
        :predicate ((print-value first) rest)
        :ref ((print-value first) rest)
        :objectList ((print-value first) rest)
        :object ((print-value first) rest)
        :prefix ((print-value first) rest)
        :PrefixedName ((print-value first) rest)
        :a ((print-value first) rest)
        ((print-value (str "DEFAULT met first:" first)) rest))
      (apply vm2rdf-edn rest))
    (apply vm2rdf-edn rest))))

(def tst-vec (parse input-file))

(def base-prefix "pref")

(def prefix-counter (atom 0))

(def dataset (atom {:prefix {}
                    :triples #{}}))

(def local-prefixes (atom {}))

(def triple (atom []))

(defn build-prefix
  []
  (let [p-cnt (swap! prefix-counter inc)
        prefix (str base-prefix p-cnt)]
    (keyword prefix)))

(defn register-prefix
  [prefix iri]
  (swap! dataset update :prefix conj {prefix iri}))

(defn return-prefix
  "Returns the correct prefix for a namespace.
  In case a prefix is supplied, it retrieves the namespace, to make sure it isn't used already.
  When the namespace is already used, the existing prefix for this namespace will be supplied.
  Otherwhise the prefix with it's original namespace will be registered."
  [ns]
  (let [local-prefs @local-prefixes
        absolute-iri (local-prefs ns)
        prefixes (@dataset :prefix)
        nses (map-invert prefixes)]
    (if (string? ns)
      (or (nses ns) (build-prefix))
      (name (or (nses absolute-iri) (do (register-prefix ns absolute-iri) ns))))))

(defn prefixed-name
  [value]
  (let [prefix (return-prefix (keyword (first value)))
        e (count value)]
    (cond
      (= e 3) (keyword (str prefix (apply str (rest value))))
      (= e 4) (keyword (str prefix "/" (apply str (rest (rest value)))))
      :else (println "No match for count of elements in value:" e "value:" value))))

(defprotocol IriTypes
  "allows the handling of different iri datatypes"
  (iri [value] "Converts iri to correct value for vector-triple"))

(extend-type java.lang.String
  IriTypes
  (iri [value] (str "dit is een java.lang.String " value)
    (let [[ns term] (rest (re-matches #"^(http[^\s]+[/#])([^/#].*)$" value))
          prefix (return-prefix ns)]
      (swap! dataset assoc :prefix {prefix ns})
      (keyword (str (name prefix) "/" term)))))

(extend-type clojure.lang.PersistentVector
  IriTypes
  (iri [value] (str "En dit is de vector " value)
    (condp = (value 0)
      :ref (keyword (str "base/" (value 1)))
      :PrefixedName (prefixed-name (rest value))
      (println "This vector iri does not resovle:" value))))

(defn resource
  [r dataset]
  (let [base (:base dataset)
        prefix (:prefix dataset)
        triples (:triples dataset)]
    (condp = (r 0)
      :iri (iri (r 1))
      :a (do (register-prefix :rdf "http://www.w3.org/1999/02/22-rdf-syntax-ns#")
             (keyword "rdf/type")))))

(defn process-resource
  [ti]
  (println "ti:" ti)
  (if (= ti :triples)
    nil
    (condp = (ti 0)
      :subject (swap! triple assoc 0 (resource (ti 1) @dataset))
      :predicateObjectList (map process-resource (rest ti))
      :predicate (swap! triple assoc 1 (resource (ti 1) @dataset))
      :objectList (map process-resource (rest ti))
      :object (do
                (swap! triple assoc 2 (resource (ti 1) @dataset))
                (swap! dataset update :triples conj @triple)
                (reset! triple []))
      (println "End of Condp ti 0:" (ti 0) "-ti:" ti "-triple:" @triple))))

(defn process-triples
  [symbol]
  (println "symbol:" symbol)
  (for [ti symbol]
    (process-resource ti)))

(defn prefix-namespace
  "Returns absolute url namespace.
  When nspace is a vector it asumes that position zero has
  the :ref keyword and position one of the vector contains the relative path of the namespace"
  [nspace]
  (if (vector? nspace)
    (str (:base (:prefix @dataset)) (nspace 1))
    nspace))

(defn process-prefix
  [statement]
  (let [e (count statement)]
    (cond
      (= e 3) (swap! local-prefixes conj
                                     {(keyword (statement 1)) (prefix-namespace ((statement 2) 1))})
      (= e 4) (swap! local-prefixes conj
                                     {(keyword (statement 1)) (prefix-namespace ((statement 3) 1))})
      :else (println "Incorrect amount of elements, count is:" (count statement) "The failing statement:" statement))))

(defn process-base
  [base]
  (println "base:" base)
  (swap! dataset update :prefix conj {:base ((base 1) 1)}))

(defn statement-type
  [vm]
  (for [symbol (rest vm)]
    (let [kw (first symbol)]
      (if (keyword? kw)
        (condp = kw
          :triples (process-triples symbol)
          :prefix (process-prefix symbol)
          :base (process-base symbol))))))