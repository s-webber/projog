?- pj_add_predicate(meta_data/2, 'org.projog.core.predicate.udp.PredicateMetaData').

p1(a) :- write(a).
p1(b) :- write(b).
p1(c) :- write(c).

p2(d) :- write(d).
p2(e) :- write(e).
p2(f) :- write(f).

p3(g) :- write(g).
p3(h) :- write(h).
p3(i) :- write(i).

p4(x) :- p1(b), p2(d), p3(i).
p4(y) :- p1(a), p2(f), p3(h).
p4(z) :- p1(c), p2(e), p3(g).

p5(X) :- p4(X).

p6 :- p5(x).

%QUERY p5(X)
%OUTPUT bdi
%ANSWER X=x
%OUTPUT afh
%ANSWER X=y
%OUTPUT ceg
%ANSWER X=z

%QUERY p5(x)
%OUTPUT bdi
%ANSWER/

%QUERY p5(y)
%OUTPUT afh
%ANSWER/

%QUERY p5(z)
%OUTPUT ceg
%ANSWER/

%FALSE p5(w)

%QUERY p6
%OUTPUT bdi
%ANSWER/

%QUERY meta_data(p4(q), X)
%ANSWER X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X = factory_isRetryable : true
%ANSWER X = factory_isAlwaysCutOnBacktrack : false
%ANSWER X = actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$LinkedHashMapPredicateFactory
%ANSWER X = actual_isRetryable : true
%ANSWER X = actual_isAlwaysCutOnBacktrack : false
%ANSWER X = processed_class : org.projog.core.predicate.udp.NeverSucceedsPredicateFactory
%ANSWER X = processed_isRetryable : false
%ANSWER X = processed_isAlwaysCutOnBacktrack : false

%QUERY meta_data(p4(x), X)
%ANSWER X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X = factory_isRetryable : true
%ANSWER X = factory_isAlwaysCutOnBacktrack : false
%ANSWER X = actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$LinkedHashMapPredicateFactory
%ANSWER X = actual_isRetryable : true
%ANSWER X = actual_isAlwaysCutOnBacktrack : false
%ANSWER X = processed_class : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory
%ANSWER X = processed_isRetryable : false
%ANSWER X = processed_isAlwaysCutOnBacktrack : false

%QUERY meta_data(p5(q), X)
%ANSWER X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X = factory_isRetryable : true
%ANSWER X = factory_isAlwaysCutOnBacktrack : false
%ANSWER X = actual_class : org.projog.core.predicate.udp.SingleRetryableRulePredicateFactory
%ANSWER X = actual_isRetryable : true
%ANSWER X = actual_isAlwaysCutOnBacktrack : false
%ANSWER X = processed_class : org.projog.core.predicate.udp.SingleRetryableRulePredicateFactory
%ANSWER X = processed_isRetryable : true
%ANSWER X = processed_isAlwaysCutOnBacktrack : false

%QUERY meta_data(p5(x), X)
%ANSWER X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X = factory_isRetryable : true
%ANSWER X = factory_isAlwaysCutOnBacktrack : false
%ANSWER X = actual_class : org.projog.core.predicate.udp.SingleRetryableRulePredicateFactory
%ANSWER X = actual_isRetryable : true
%ANSWER X = actual_isAlwaysCutOnBacktrack : false
%ANSWER X = processed_class : org.projog.core.predicate.udp.SingleRetryableRulePredicateFactory
%ANSWER X = processed_isRetryable : true
%ANSWER X = processed_isAlwaysCutOnBacktrack : false

%QUERY meta_data(p6, X)
%ANSWER X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X = factory_isRetryable : true
%ANSWER X = factory_isAlwaysCutOnBacktrack : false
%ANSWER X = actual_class : org.projog.core.predicate.udp.SingleRetryableRulePredicateFactory
%ANSWER X = actual_isRetryable : true
%ANSWER X = actual_isAlwaysCutOnBacktrack : false
%ANSWER X = processed_class : org.projog.core.predicate.udp.SingleRetryableRulePredicateFactory
%ANSWER X = processed_isRetryable : true
%ANSWER X = processed_isAlwaysCutOnBacktrack : false

