(ns clj-tparse.core
  (:require [instaparse.core :as insta]))


(def turtle-config-file "resources/rdf-turtle-spec.txt")
(def input-file "resources/example0.ttl")

(def resultset-map (atom {}))

;; TODO convert turtle input to EDN triples
;; - TODO 1 function for parsing turtle file with Instaparse output
;; - TODO 2 Convert Instaparse output to EDN


(defn read-file
  []
  (slurp input-file))

(defn read-line
  [file]
  (doseq [line (clojure.string/split-lines file)]
    (println "Een:" line)))


(defn start!
  []
  (println "function to start clj-tparse"))

(comment (defn line [n filename]
           (with-open [rdr (io/reader filename)]
             (doall (drop (- n 1) (take n (line-seq rdr))))))

         (def turtle
           (insta/parser
             (slurp turtle-tst-file) :trace true)))