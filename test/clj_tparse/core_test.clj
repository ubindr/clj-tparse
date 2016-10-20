(ns clj-tparse.core-test
  (:require [clojure.test :refer :all]
            [instaparse.core :as insta]))

;; input files for tests
(def turtle-file "resources/example.ttl")
(def turtle-file0 "resources/example0.ttl")
(def turtle-file1 "resources/example1.ttl")
(def turtle-file2 "resources/example2.ttl")
(def turtle-file3 "resources/example3.ttl")
(def turtle-file4 "resources/example4.ttl")
(def turtle-file5 "resources/example5.ttl")
(def turtle-file6 "resources/example6.ttl")
(def turtle-file7 "resources/example7.ttl")
(def turtle-file8 "resources/example8.ttl")
(def turtle-file9 "resources/example9.ttl")
(def turtle-file10 "resources/example10.ttl")
(def turtle-file11 "resources/example11.ttl")

;; grammar definition file
(def turtle-config-file "resources/rdf-turtle-spec.txt")

(defn parse-file
  [input-file parser]
  (insta/parse parser (slurp input-file)))

(deftest test-turtle-config
  (testing "testing EBNF config-file for turtle"
    (let [turtle-config (insta/parser turtle-config-file)]
      (is (vector? (parse-file turtle-file turtle-config)) "Result for test-file resources/example.ttl")
      (is (vector? (parse-file turtle-file0 turtle-config)) "Result for test-file resources/example0.ttl")
      (is (vector? (parse-file turtle-file1 turtle-config)) "Result for test-file resources/example1.ttl")
      (is (vector? (parse-file turtle-file2 turtle-config)) "Result for test-file resources/example2.ttl")
      (is (vector? (parse-file turtle-file3 turtle-config)) "Result for test-file resources/example3.ttl")
      (is (vector? (parse-file turtle-file4 turtle-config)) "Result for test-file resources/example4.ttl")
      (is (vector? (parse-file turtle-file5 turtle-config)) "Result for test-file resources/example5.ttl")
      (is (vector? (parse-file turtle-file6 turtle-config)) "Result for test-file resources/example6.ttl")
      (is (vector? (parse-file turtle-file7 turtle-config)) "Result for test-file resources/example7.ttl")
      (is (vector? (parse-file turtle-file8 turtle-config)) "Result for test-file resources/example8.ttl")
      (is (vector? (parse-file turtle-file9 turtle-config)) "Result for test-file resources/example9.ttl")
      (is (vector? (parse-file turtle-file10 turtle-config)) "Result for test-file resources/example10.ttl")
      (is (vector? (parse-file turtle-file11 turtle-config)) "Result for test-file resources/example11.ttl"))))
