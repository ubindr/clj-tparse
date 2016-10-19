(ns clj-tparse.examples.edn0)

(def sample
  {:base   "http://one.example/"
   :prefix {:p   "http://two.example/"
            :ns1 "http://one.example/path/"
            :-   "http://another.example/"
            :rdf "http://www.w3.org/1999/02/22-rdf-syntax-ns#"}
   :data   {:base/subject1 {:base/predicate1 :base/object1}
            :base/subject2 {:base/predicate2 :base/object2}
            :p/subject3    {:p/predicate3 :p/object3}
            :ns1/subject4 {:ns1/predicate4 :ns1/object4}
            :-/subject5 {:-/predicate5 :-/object5}
            :-/subject6 {:rdf/type :-/subject7}}})



