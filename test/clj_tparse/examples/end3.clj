(ns clj-tparse.examples.end3)

(def sample
  {:prefix {:- "http://example.org/stuff/1.0/"}
   :data   {:-/a {:-/b "The first line\nThe second line\n  more"}
            :-/a {:-/b "The first line\nThe second line\n  more"}}})
