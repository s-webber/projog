?- pj_add_predicate(meta_data/2, 'org.projog.core.predicate.udp.PredicateMetaData').

p(a, 1, X) :- repeat.
p(a, 2, X) :- !, repeat.
p(a, 3, X) :- repeat, !.
p(a, 4, X).
p(b, 1, X) :- repeat.
p(b, 2, X) :- var(X), !.
p(b, 2, X). 
p(c, 3, x).
p(c, 3, y).
p(c, 3, z).
p(c, 4, x) :- repeat, !.
p(c, 4, y).
p(d, 1, X) :- !.
p(d, 2, X) :- repeat, !.
p(d, 2, X).
p(e, 1, X) :- repeat.
p(e, 2, X).
p(e, 2, X).
p(f, 1, x).
p(f, 2, y).
p(f, 3, z).
p(g, 1, x) :- !.
p(g, 2, y) :- !.
p(g, 3, z).

% SingleRetryableRulePredicateFactory retryable, no cut
%QUERY meta_data(p(a, 1, _), X)
%ANSWER X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X = factory_isRetryable : true
%ANSWER X = factory_isAlwaysCutOnBacktrack : false
%ANSWER X = actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
%ANSWER X = actual_isRetryable : true
%ANSWER X = actual_isAlwaysCutOnBacktrack : false
%ANSWER X = processed_class : org.projog.core.predicate.udp.SingleRetryableRulePredicateFactory
%ANSWER X = processed_isRetryable : true
%ANSWER X = processed_isAlwaysCutOnBacktrack : false

% SingleRetryableRulePredicateFactory retryable, has cut
%QUERY meta_data(p(a, 2, _), X)
%ANSWER X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X = factory_isRetryable : true
%ANSWER X = factory_isAlwaysCutOnBacktrack : false
%ANSWER X = actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
%ANSWER X = actual_isRetryable : true
%ANSWER X = actual_isAlwaysCutOnBacktrack : false
%ANSWER X = processed_class : org.projog.core.predicate.udp.SingleRetryableRulePredicateFactory
%ANSWER X = processed_isRetryable : true
%ANSWER X = processed_isAlwaysCutOnBacktrack : false

% SingleNonRetryableRulePredicateFactory - non retryable, has cut
%QUERY meta_data(p(a, 3, _), X)
%ANSWER X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X = factory_isRetryable : true
%ANSWER X = factory_isAlwaysCutOnBacktrack : false
%ANSWER X = actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
%ANSWER X = actual_isRetryable : true
%ANSWER X = actual_isAlwaysCutOnBacktrack : false
%ANSWER X = processed_class : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory
%ANSWER X = processed_isRetryable : false
%ANSWER X = processed_isAlwaysCutOnBacktrack : false

% SingleNonRetryableRulePredicateFactory - non retryable, no cut
%QUERY meta_data(p(a, 4, _), X)
%ANSWER X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X = factory_isRetryable : true
%ANSWER X = factory_isAlwaysCutOnBacktrack : false
%ANSWER X = actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
%ANSWER X = actual_isRetryable : true
%ANSWER X = actual_isAlwaysCutOnBacktrack : false
%ANSWER X = processed_class : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory
%ANSWER X = processed_isRetryable : false
%ANSWER X = processed_isAlwaysCutOnBacktrack : false

% IndexablePredicateFactory - retryable
%QUERY meta_data(p(f, Y, Z), X)
%ANSWER
% X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = factory_isRetryable : true
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = factory_isAlwaysCutOnBacktrack : false
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = actual_isRetryable : true
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = actual_isAlwaysCutOnBacktrack : false
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = processed_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = processed_isRetryable : true
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = processed_isAlwaysCutOnBacktrack : false
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER

% IndexablePredicateFactory not retryable
%QUERY meta_data(p(g, Y, Z), X)
%ANSWER
% X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = factory_isRetryable : true
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = factory_isAlwaysCutOnBacktrack : false
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = actual_isRetryable : true
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = actual_isAlwaysCutOnBacktrack : false
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = processed_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = processed_isRetryable : false
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = processed_isAlwaysCutOnBacktrack : false
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER

% SingleIndexPredicateFactory retryable
%QUERY meta_data(p(b, Y, Z), X)
%ANSWER
% X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = factory_isRetryable : true
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = factory_isAlwaysCutOnBacktrack : false
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = actual_isRetryable : true
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = actual_isAlwaysCutOnBacktrack : false
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = processed_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$SingleIndexPredicateFactory
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = processed_isRetryable : true
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = processed_isAlwaysCutOnBacktrack : false
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER

% SingleIndexPredicateFactory not retryable
%QUERY meta_data(p(d, Y, Z), X)
%ANSWER
% X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = factory_isRetryable : true
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = factory_isAlwaysCutOnBacktrack : false
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = actual_isRetryable : true
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = actual_isAlwaysCutOnBacktrack : false
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = processed_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$SingleIndexPredicateFactory
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = processed_isRetryable : false
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = processed_isAlwaysCutOnBacktrack : false
% Y = UNINSTANTIATED VARIABLE
% Z = UNINSTANTIATED VARIABLE
%ANSWER

% LinkedHashMapPredicateFactory retryable
%QUERY meta_data(p(c, 3, Y), X)
%ANSWER
% X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = factory_isRetryable : true
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = factory_isAlwaysCutOnBacktrack : false
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = actual_isRetryable : true
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = actual_isAlwaysCutOnBacktrack : false
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = processed_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$LinkedHashMapPredicateFactory
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = processed_isRetryable : true
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = processed_isAlwaysCutOnBacktrack : false
% Y = UNINSTANTIATED VARIABLE
%ANSWER

% LinkedHashMapPredicateFactory not retryable
%QUERY meta_data(p(c, 4, Y), X)
%ANSWER
% X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = factory_isRetryable : true
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = factory_isAlwaysCutOnBacktrack : false
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = actual_isRetryable : true
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = actual_isAlwaysCutOnBacktrack : false
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = processed_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$LinkedHashMapPredicateFactory
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = processed_isRetryable : false
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = processed_isAlwaysCutOnBacktrack : false
% Y = UNINSTANTIATED VARIABLE
%ANSWER

% NotIndexablePredicateFactory retryable
%QUERY meta_data(p(e, 2, Y), X)
%ANSWER
% X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = factory_isRetryable : true
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = factory_isAlwaysCutOnBacktrack : false
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = actual_isRetryable : true
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = actual_isAlwaysCutOnBacktrack : false
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = processed_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$NotIndexablePredicateFactory
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = processed_isRetryable : true
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = processed_isAlwaysCutOnBacktrack : false
% Y = UNINSTANTIATED VARIABLE
%ANSWER

% NotIndexablePredicateFactory not retryable
%QUERY meta_data(p(b, 2, Y), X)
%ANSWER
% X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = factory_isRetryable : true
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = factory_isAlwaysCutOnBacktrack : false
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = actual_isRetryable : true
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = actual_isAlwaysCutOnBacktrack : false
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = processed_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$NotIndexablePredicateFactory
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = processed_isRetryable : false
% Y = UNINSTANTIATED VARIABLE
%ANSWER
%ANSWER
% X = processed_isAlwaysCutOnBacktrack : false
% Y = UNINSTANTIATED VARIABLE
%ANSWER

