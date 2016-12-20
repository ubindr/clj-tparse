# clj-tparse

Clojure library to parse [RDF](https://www.w3.org/TR/rdf11-concepts/) [turtle syntax](https://www.w3.org/TR/turtle/) files into [edn](https://github.com/edn-format/edn) triples.

## Usage

The main functions of this library (in clj-tparse.core):
- parse-turtle
- parse-statements

#### parse-turtle
Supply a turtle file and returns triples in hiccup-style nested vectors.
Supplies a turtle definition file, in EBNF format, to insta-parse for parsing the supplied file.

```clojure
(parse-turtle "resources/example.ttl")
```

#### parse-statements
Takes triples in hiccup-style nested vectors, as returned by parse-turtle. Returns edn-triples

```clojure
(parse-statements (parse-turtle "resources/example.ttl"))
```

## edn-triples
Clj-tparse uses the following structures to encode RDF triples in edn.
- prefix; a map of prefix and uri - `{:local "http://example.org/stuff/1.0/", :rdf "http://www.w3.org/1999/02/22-rdf-syntax-ns#"}`
- turtle document; a set of triples - `#{<triples>}`
- triple; a vector containing a statement - `[<statement>]`
- statement; three resources (a triple) - `resource resource resource`
- resource; primarily a namespaced keyword or a literal, blank-node or collection - `:rdf/type`
- literal; a string without type - `"<string>"`
- typed-literal; a map with :value and :lang or :type keys - `{:value "That Seventies Show", :type :xsd/string}` or `{:value "That Seventies Show", :lang "en"}`
- blank-node; a map with two resources or a keyword with `_` as namespace - `{:foaf/name "Alice"}` or `:_/bob`
- RDF collection; a vector containing resources - `[1 2.0M 3E+1M]`

## Exception
RDF Resource names starting with a number, will have '#-remove-#' added in front of the number. 
Because Clojure does not support the number at the beginning of a keyword name.

## Output samples
sample 1:
```clojure
#{[:_/bob :foaf/knows :_/alice]
  [:_/alice :foaf/knows :_/bob]
  [{:empty :blank-node} :foaf/knows {:foaf/name "Bob"}]}
```

sample 2:
```clojure
#{[:show/#-remove-#218 :show/localName {:value "Cette Série des Années Soixante-dix", :lang "fr"}]
  [:show/#-remove-#218 :rdfs/label {:value "That Seventies Show", :type :xsd/string}]
  [:show/#-remove-#218 :rdfs/label "That Seventies Show"]
  [:show/#-remove-#218 :show/localName {:value "That Seventies Show", :lang "en"}]
  [:show/#-remove-#218 :show/localName {:value "Cette Série des Années Septante", :lang "fr-be"}]}
```

sample 3:
```clojure
#{[:local/subject6 :rdf/type :local/subject7]
  [:p/subject3 :p/predicate3 :p/object3]
  [:base/subject2 :base/predicate2 :base/object2]
  [:local/subject5 :local/predicate5 :local/object5]
  [:ns2/subject4 :ns2/predicate4 :ns2/object4]
  [:ns1/subject1 :ns1/predicate1 :ns1/object1]}
```

full dataset sample:
```clojure
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
            [:ns1/subject1 :ns1/predicate1 :ns1/object1]}}
```
See also core-test for more samples

## To do
- Resolve ubiquity of blank-node and typed-literal
- Improve performance, build full turtle parser (without the need for insta-parse)

## Special Thanks
[Mark Engelberg](https://github.com/Engelberg) for writing [insta-parse](https://github.com/Engelberg/instaparse#instaparse-143). An amazing tool to parse data described in EBFN (and some other syntaxes) to a clojure edn structure.


## License

Copyright © 2016 Richard Nagelmaeker

Distributed under the Eclipse Public License version 1.0
