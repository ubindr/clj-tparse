(ns clj-tparse.core
  (:require [instaparse.core :as insta]))


(def turtle-config-file "resources/rdf-turtle-spec.txt")
(def turtle-tst-file "resources/tst-rdf-turtle-11-ebfn-spec.txt")
(def input-file "resources/example1.ttl")
(def line-counter (atom 0))

(def resultset-map (atom {}))

(defn read-file
  []
  (slurp input-file))

;; TODO read inputfile line by line
;; TODO decide the line type (base, prefix, triple or multi-line)
;; TODO proces input based on line type

(defn parse-file
  [file]
  (doseq [line (clojure.string/split-lines file)]
    (swap! line-counter inc)
    (println "Weer een:" @line-counter ":" line)))


(defn start!
  []
  (parse-file (read-file)))

(comment (defn line [n filename]
           (with-open [rdr (io/reader filename)]
             (doall (drop (- n 1) (take n (line-seq rdr))))))

         (def turtle
           (insta/parser
             (slurp turtle-tst-file) :trace true)))