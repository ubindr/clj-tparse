(ns clj-tparse.examples.edn1)

(def sample
  {:prefix {:rdf "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
            :dc  "http://purl.org/dc/elements/1.1/"
            :ex  "http://example.org/stuff/1.0/"
            :ns1 "http://www.w3.org/TR/"
            :ns2 "http://purl.org/net/dajobe/"}

   :data   {:ns1/rdf-syntax-grammar {:dc/title  "RDF/XML Syntax Specification (Revised)"
                                     :ex/editor {:ex/fullname "Dave Beckett"
                                                 :ex/homePage :ns2/_}}}})
