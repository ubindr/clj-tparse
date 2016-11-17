(ns clj-tparse.core-test
  "Testing the clj-tparse parser"
  (:require [clojure.test :refer :all]
            [instaparse.core :as insta]
            [clj-tparse.core :refer [statement-type dataset parsing-prefixes triple prefix-counter]]))

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

(defn parser
  []
  (insta/parser turtle-config-file))

(defn reset-datasets
  []
  (reset! dataset {:prefix  {}
                   :triples #{}})
  (reset! parsing-prefixes {})
  (reset! triple [])
  (reset! prefix-counter 0) nil)

(defn edn-triples
  [input-file]
  (reset-datasets)
  (statement-type (parse-file input-file (parser))))

(deftest test-example
  (testing "Does example.ttl parse succesfully?"
    (let [input-file turtle-file
          resultset @dataset]
      (edn-triples input-file)
      (is (= resultset
             {:prefix {:base "http://example.org/",
                       :rel "http://www.perceive.net/schemas/relationship/",
                       :rdf "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
                       :foaf "http://xmlns.com/foaf/0.1/"},
              :triples #{[:base/#spiderman :rdf/type :foaf/Person]
                         [:base/#green-goblin :foaf/name "Green Goblin"]
                         [:base/#spiderman :foaf/name {:value "Человек-паук", :lang "ru"}]
                         [:base/#spiderman :rel/enemyOf :base/#green-goblin]
                         [:base/#spiderman :foaf/name "Spiderman"]
                         [:base/#green-goblin :rel/enemyOf :base/#spiderman]
                         [:base/#green-goblin :rdf/type :foaf/Person]}})
          "Result for test-file resources/example.ttl"))))

(deftest test-example0
  (testing "Does example.ttl parse succesfully?"
    (let [input-file turtle-file0]
      (edn-triples input-file)
      (is (= @dataset
             {:prefix {:ns1 "http://one.example/",
                       :base "http://one.example/",
                       :p "http://two.example/",
                       :ns2 "http://one.example/path/",
                       :local "http://another.example/",
                       :rdf "http://www.w3.org/1999/02/22-rdf-syntax-ns#"},
              :triples #{[:local/subject6 :rdf/type :local/subject7]
                         [:p/subject3 :p/predicate3 :p/object3]
                         [:base/subject2 :base/predicate2 :base/object2]
                         [:local/subject5 :local/predicate5 :local/object5]
                         [:ns2/subject4 :ns2/predicate4 :ns2/object4]
                         [:ns1/subject1 :ns1/predicate1 :ns1/object1]}})
          "Result for test-file resources/example.ttl"))))

(deftest test-example1
  (testing "Does example.ttl parse succesfully?"
    (let [input-file turtle-file1]
      (edn-triples input-file)
      (is (= @dataset
             {:prefix {:ns1 "http://www.w3.org/TR/", :dc "http://purl.org/dc/elements/1.1/", :ex "http://example.org/stuff/1.0/"},
              :triples #{[:ns1/rdf-syntax-grammar
                          :ex/editor
                          {:ex/fullname "Dave Beckett", :ex/homePage "http://purl.org/net/dajobe/"}]
                         [:ns1/rdf-syntax-grammar :dc/title "RDF/XML Syntax Specification (Revised)"]}})
          "Result for test-file resources/example.ttl"))))

(deftest test-example2
  (testing "Does example.ttl parse succesfully?"
    (let [input-file turtle-file2]
      (edn-triples input-file)
      (is (= @dataset
             {:prefix {:local "http://example.org/stuff/1.0/", :rdf "http://www.w3.org/1999/02/22-rdf-syntax-ns#"},
              :triples #{[:local/a :local/b {:rdf/first "apple", :rdf/rest {:rdf/first "banana", :rdf/rest :rdf/nil}}]}})
          "Result for test-file resources/example.ttl"))))

(deftest test-example3
  (testing "Does example.ttl parse succesfully?"
    (let [input-file turtle-file3]
      (edn-triples input-file)
      (is (= @dataset
             {:prefix {:local "http://example.org/stuff/1.0/"},
              :triples #{[:local/a :local/b "The first line\nThe second line\n  more"]
                         [:local/a :local/b "The first line\\nThe second line\\n  more"]}})
          "Result for test-file resources/example.ttl"))))

(deftest test-example4
  (testing "Does example.ttl parse succesfully?"
    (let [input-file turtle-file4]
      (edn-triples input-file)
      (is (= @dataset
             {:prefix {:local "http://example.org/stuff/1.0/"}, :triples #{[[1 2.0M 3E+1M] :local/p "w"]}})
          "Result for test-file resources/example.ttl"))))

(deftest test-example5
  (testing "Does example.ttl parse succesfully?"
    (let [input-file turtle-file5]
      (edn-triples input-file)
      (is (= @dataset
             {:prefix {:local "http://example.org/stuff/1.0/"}, :triples #{[[1 {:local/p :local/q} [2]] :local/p2 :local/q2]}})
          "Result for test-file resources/example.ttl"))))

(deftest test-example6
  (testing "Does example.ttl parse succesfully?"
    (let [input-file turtle-file6]
      (edn-triples input-file)
      (is (= @dataset
             {:prefix {:show "http://example.org/vocab/show/",
                       :rdfs "http://www.w3.org/2000/01/rdf-schema#",
                       :xsd "http://www.w3.org/2001/XMLSchema#"},
              :triples #{[:show/#218 :rdfs/label "That Seventies Show"]
                         [:show/#218 :show/localName {:value "Cette Série des Années Septante", :lang "fr-be"}]
                         [:show/#218 :show/localName {:value "That Seventies Show", :lang "en"}]
                         [:show/#218
                          :show/blurb
                          "This is a multi-line                        # literal with embedded new lines and quotes
                           literal with many quotes (\"\"\"\"\")
                           and up to two sequential apostrophes ('')."]
                         [:show/#218 :rdfs/label {:value "That Seventies Show", :type :xsd/string}]
                         [:show/#218 :show/localName {:value "Cette Série des Années Soixante-dix", :lang "fr"}]}})
          "Result for test-file resources/example.ttl"))))

(deftest test-example7
  (testing "Does example.ttl parse succesfully?"
    (let [input-file turtle-file7]
      (edn-triples input-file)
      (is (= @dataset
             {:prefix {:ns1 "http://en.wikipedia.org/wiki/", :local "http://example.org/elements"},
              :triples #{[:ns1/Helium :local/specificGravity 0.0001663M]
                         [:ns1/Helium :local/atomicMass 4.002602M]
                         [:ns1/Helium :local/atomicNumber 2]}})
          "Result for test-file resources/example.ttl"))))

(deftest test-example8
  (testing "Does example.ttl parse succesfully?"
    (let [input-file turtle-file8]
      (edn-triples input-file)
      (is (= @dataset
             {:prefix {:ns1 "http://somecountry.example/", :local "http://example.org/stats"},
              :triples #{[:ns1/census2007 :local/isLandlocked "false"]}})
          "Result for test-file resources/example.ttl"))))

(deftest test-example9
  (testing "Does example.ttl parse succesfully?"
    (let [input-file turtle-file9]
      (edn-triples input-file)
      (is (= @dataset
             {:prefix {:foaf "http://xmlns.com/foaf/0.1/"},
              :triples #{[:_/bob :foaf/knows :_/alice]
                         [:_/alice :foaf/knows :_/bob]
                         [{:empty :blank-node} :foaf/knows {:foaf/name "Bob"}]}})
          "Result for test-file resources/example.ttl"))))

(deftest test-example10
  (testing "Does example.ttl parse succesfully?"
    (let [input-file turtle-file10]
      (edn-triples input-file)
      (is (= @dataset
             {:prefix {:foaf "http://xmlns.com/foaf/0.1/"},
              :triples #{[{:foaf/name "Alice"}
                          :foaf/knows
                          {:foaf/name "Bob", :foaf/knows {:foaf/name "Eve"}, :foaf/mbox "bob@example.com"}]}})
          "Result for test-file resources/example.ttl"))))

(deftest test-example11
  (testing "Does example.ttl parse succesfully?"
    (let [input-file turtle-file11]
      (edn-triples input-file)
      (is (= @dataset
             {:prefix {:ns1 "http://xmlns.com/foaf/0.1/"},
              :triples #{[:_/a :ns1/knows :_/b]
                         [:_/a :ns1/name "Alice"]
                         [:_/b :ns1/mbox "bob@example.com"]
                         [:_/b :ns1/name "Bob"]
                         [:_/b :ns1/knows :_/c]
                         [:_/c :ns1/name "Eve"]}})
          "Result for test-file resources/example.ttl"))))