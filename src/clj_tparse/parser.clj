(ns clj-tparse.parser
  (:require [clj-tparse.lexer :refer [lexer symbols]]
            [clojure.string :as str]
            [clojure.set :refer [map-invert]]))

(declare accept!)

;; Temporary development variables

(def tst-input "resources/example1.ttl")

;; Constants

(def base-prefix "ns")

;; State variables

(def parsing-prefixes (atom {}))
(def dataset (atom {:prefixes {}
                    :triples {}}))
(def prefix-counter (atom 0))

;; -- Regular expressions for symbols --

;; Symbols used in other functions.

(def split-iri (re-pattern "^<(http://[^\u0000-\u0020<>\"{}\\|^`]+[/#]{1})([^\u0000-\u0020<>\"{}|^`\\/]+)>$"))
(def match-ns (re-pattern "^(http://[^\u0000-\u0020<>\"{}\\|^`]+[/#]{1})$"))

;; Symbols for accept! function

(def prefix-label (partial accept! "(^@prefix$|^prefix$|^PREFIX$){1}"))
(def base-label (partial accept! "(^@base$|^base$|^BASE$){1}"))

(defn nth-rest-vector
  [n]
  (fn [c] (vec (nthrest c n))))

(defn next-symbol!
  ([] (next-symbol! 1))
  ([n] (swap! symbols (nth-rest-vector n))))

(defn accept!
  [re-s]
  (if-let [current-symbol (first @symbols)]
    (do
      (if (re-matches (re-pattern re-s) current-symbol)
        (do
          (next-symbol!)
          true)
        false))
    nil))                                                   ;; Signaling symbols is empty

(defn generate-prefix
  []
  (let [p-cnt (swap! prefix-counter inc)
        prefix (str base-prefix p-cnt)]
    (keyword prefix)))

(defn register-prefix
  [prefix iri]
  (swap! dataset assoc :prefixes {prefix iri}))

(defn return-prefix
  "Returns the correct prefix for a namespace.
  In case a prefix is supplied, it retrieves the namespace, to make sure it isn't used already.
  When the namespace is already used, the existing prefix for this namespace will be supplied.
  Otherwhise the prefix with it's original namespace will be registered."
  [nspace]
  (if (= "_" nspace)
    (keyword nspace)
    (let [ns (if (= ":" nspace) "local" nspace)
          lp-map @parsing-prefixes
          gp-map (@dataset :prefixes)
          absolute-url (or (first (re-matches match-ns ns)) (lp-map (keyword ns)) (gp-map (keyword ns)))
          local-nses (map-invert lp-map)
          nses (map-invert gp-map)
          a (or (and (local-nses absolute-url) 2) 0)
          b (or (and (nses absolute-url) 1) 0)
          g-prefix (nses absolute-url)
          l-prefix (local-nses absolute-url)]
      (println "\nfn: return-prefix\n- prefix location state count:" (+ a b) "\n")
      (condp = (+ a b)
        0 (let [prefix (generate-prefix)]
            (register-prefix prefix absolute-url)
            prefix)
        1 g-prefix
        2 (let [prefix (or (if (gp-map l-prefix)
                             false
                             l-prefix)
                           (generate-prefix))]
            (register-prefix prefix absolute-url) prefix)
        3 g-prefix
        (println "\nfn: return-prefix\n- Unexpected prefix state - parsing-prefixes:" a "- global-prefixes:" b "\n")))))

(defn iri!
  []
  (let [iri (first @symbols)]
    (->> (re-matches split-iri iri)
         ((juxt #(return-prefix (% 1)) #(% 2)))
         (apply #(format "%s/%s" %1 %2))
         rest
         (apply str)
         keyword)))

(defn blank-node!
  [])

(defn list!
  [])

(defn prefixed-name!
  [])

(defn next-statement
  []
  (println "\nfn: next-statement\n- first @symbols:" (first @symbols) "\n")
  (let [punctuation (first @symbols)]))

(defn object
  []
  (println "\nfn: object!\n- first @symbols:" (first @symbols) "\n")
  (let [object (first @symbols)]
    ))

(defn predicate
  []
  (println "\nfn: predicate!\n- first @symbols:" (first @symbols) "\n")
  (let [predicate (first @symbols)]
    ))

(defn subject
  []
  (println "\nfn: subject!\n- first @symbols:" (first @symbols) "\n")
  (let [subject (first @symbols)]
    (condp = (first subject)
      \< iri!
      \[ blank-node!
      \( list!
      :default prefixed-name!)))

(defn triple
  []
  (println "\nfn: triple!\n- first @symbols:" (first @symbols) "\n")
  (do
    (subject)
    (predicate)
    (object)
    (next-statement)))

(defn base!
  []
  (println "\nfn: base!\n- first @symbols:" (first @symbols) "\n")
  (next-symbol!))

(defn prefix!
  []
  (println "\nfn: prefix!\n- first @symbols:" (first @symbols)
           "\n- second @symbols:" (second @symbols) "\n")
  (let [prefix (first @symbols)
        iri (second @symbols)]
    (do
      (swap! parsing-prefixes assoc (keyword (str/replace prefix #":$" "")) (str/replace iri #"^<|>$" ""))
      (next-symbol! 2)
      (when (= (first @symbols) ".")
        (next-symbol!)))))

(defn statement!
  []
  (cond
    (base-label) (trampoline base!)
    (prefix-label) (trampoline prefix!)
    :default triple))

(defn parse-statements
  [string]
  (do
    (reset! symbols (lexer string))
    (while (not-empty @symbols) (trampoline statement!))
    (if (not-empty @symbols)
      (println "Parse Error!"))))
