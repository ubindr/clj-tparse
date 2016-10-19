(ns clj-tparse.examples.edn2)

(def sample
  {:prefix {:-   "http://example.org/stuff/1.0/"
            :rdf "http://www.w3.org/1999/02/22-rdf-syntax-ns#"}
   :data   {:-/a {:-/b {:rdf/first "apple"
                        :rdf/rest  {:rdf/first "banana"
                                    :rdf/rest  :rdf/nil}}}}})
