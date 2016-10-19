(ns clj-tparse.examples.edn)

(def sample
  {:base   "http://example.org/"

   :prefix {:-    "http://example.org/stuff/1.0/"
            :rdf  "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
            :rdfs "http://www.w3.org/2000/01/rdf-schema#"
            :foaf "http://xmlns.com/foaf/0.1/"
            :rel  "http://www.perceive.net/schemas/relationship/"}

   :data   {:base/#green-goblin {:rel/enemyOf :base/#spiderman
                                 :rdf/type    :foaf/Person
                                 :foaf/name   "Green Goblin"}
            :base/#spiderman    {:rel/enemyOf :base/#green-goblin
                                 :rdf/type    :foaf/Person
                                 :foaf/name   ('"Spiderman" #{"Человек-паук" "@ru"})}}})
