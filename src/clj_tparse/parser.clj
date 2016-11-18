(ns clj-tparse.parser
  (:require [clj-tparse.lexer :refer [lexer symbols]]
            [clojure.string :as str]
            [clojure.set :refer [map-invert]]))

(declare accept!)

(def tst-input "resources/example1.ttl")

(def parsing-prefixes (atom {}))
(def global-prefixes (atom {}))

(def match-iri (re-pattern "^<(http://[^\u0000-\u0020<>\"{}|^`\\]+/)([^\u0000-\u0020<>\"{}|^`\\/]+)>$"))

;; Regular expressions for symbols

(def prefix-label (partial accept! "(^@prefix$|^prefix$|^PREFIX$){1}"))
(def base-label (partial accept! "(^@base$|^base$|^BASE$){1}"))

(defn next-symbol!
  []
  (swap! symbols rest))

(defn next-two-symbols!
  []
  (swap! symbols #(rest (rest %))))

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
          gp-map @global-prefixes
          absolute-url (or (first (match-url ns)) (lp-map (keyword ns)) (gp-map (keyword ns)))
          local-nses (map-invert lp-map)
          nses (map-invert gp-map)
          a (or (and (local-nses absolute-url) 2) 0)
          b (or (and (nses absolute-url) 1) 0)
          g-prefix (nses absolute-url)
          l-prefix (local-nses absolute-url)]
      (println "Prefix deciscion number" (+ a b))
      (condp = (+ a b)
        0 (build-prefix)
        1 g-prefix
        2 (let [prefix (or (if (gp-map l-prefix)
                             false
                             l-prefix)
                           (build-prefix))]
            (register-prefix prefix absolute-url) prefix)
        3 g-prefix
        (println "Something different" (+ a b))))))

(defn iri!
  []
  (let [iri (first @symbols)]
    (->> (re-matches match-iri iri)
         (partial return-prefix #(nth % 1)))))

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
      (next-two-symbols!)
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
