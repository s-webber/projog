?- pj_add_predicate(meta_data/2, 'org.projog.core.predicate.udp.PredicateMetaData').

p(a, 1).
p(b, 2) :- repeat(2).
p(c, 2).
p(c, 3).
p(c, 3).
p(d, 4) :- repeat(2).
p(e(1), 5).
p(e(2), 6).
p(e(3), 7).
p(f, 8).

%QUERY meta_data(p(_, _), X)
%ANSWER X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X = factory_isRetryable : true
%ANSWER X = factory_isAlwaysCutOnBacktrack : false
%ANSWER X = actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
%ANSWER X = actual_isRetryable : true
%ANSWER X = actual_isAlwaysCutOnBacktrack : false
%ANSWER X = processed_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
%ANSWER X = processed_isRetryable : true
%ANSWER X = processed_isAlwaysCutOnBacktrack : false

%QUERY p(X,Y)
%ANSWER
% X=a
% Y=1
%ANSWER
%ANSWER
% X=b
% Y=2
%ANSWER
%ANSWER
% X=b
% Y=2
%ANSWER
%ANSWER
% X=c
% Y=2
%ANSWER
%ANSWER
% X=c
% Y=3
%ANSWER
%ANSWER
% X=c
% Y=3
%ANSWER
%ANSWER
% X=d
% Y=4
%ANSWER
%ANSWER
% X=d
% Y=4
%ANSWER
%ANSWER
% X=e(1)
% Y=5
%ANSWER
%ANSWER
% X=e(2)
% Y=6
%ANSWER
%ANSWER
% X=e(3)
% Y=7
%ANSWER
%ANSWER
% X=f
% Y=8
%ANSWER

%QUERY meta_data(p(a, _), X)
%ANSWER X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X = factory_isRetryable : true
%ANSWER X = factory_isAlwaysCutOnBacktrack : false
%ANSWER X = actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
%ANSWER X = actual_isRetryable : true
%ANSWER X = actual_isAlwaysCutOnBacktrack : false
%ANSWER X = processed_class : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory
%ANSWER X = processed_isRetryable : false
%ANSWER X = processed_isAlwaysCutOnBacktrack : false

%QUERY p(a, X)
%ANSWER X=1

%QUERY meta_data(p(_, 1), X)
%ANSWER X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X = factory_isRetryable : true
%ANSWER X = factory_isAlwaysCutOnBacktrack : false
%ANSWER X = actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
%ANSWER X = actual_isRetryable : true
%ANSWER X = actual_isAlwaysCutOnBacktrack : false
%ANSWER X = processed_class : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory
%ANSWER X = processed_isRetryable : false
%ANSWER X = processed_isAlwaysCutOnBacktrack : false

%QUERY p(X, 1)
%ANSWER X=a

%QUERY meta_data(p(b, _), X)
%ANSWER X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X = factory_isRetryable : true
%ANSWER X = factory_isAlwaysCutOnBacktrack : false
%ANSWER X = actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
%ANSWER X = actual_isRetryable : true
%ANSWER X = actual_isAlwaysCutOnBacktrack : false
%ANSWER X = processed_class : org.projog.core.predicate.udp.SingleRetryableRulePredicateFactory
%ANSWER X = processed_isRetryable : true
%ANSWER X = processed_isAlwaysCutOnBacktrack : false

%QUERY p(b, X)
%ANSWER X=2
%ANSWER X=2

%QUERY meta_data(p(_, 4), X)
%ANSWER X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X = factory_isRetryable : true
%ANSWER X = factory_isAlwaysCutOnBacktrack : false
%ANSWER X = actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
%ANSWER X = actual_isRetryable : true
%ANSWER X = actual_isAlwaysCutOnBacktrack : false
%ANSWER X = processed_class : org.projog.core.predicate.udp.SingleRetryableRulePredicateFactory
%ANSWER X = processed_isRetryable : true
%ANSWER X = processed_isAlwaysCutOnBacktrack : false

%QUERY p(X, 4)
%ANSWER X=d
%ANSWER X=d

%QUERY meta_data(p(c, _), X)
%ANSWER X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X = factory_isRetryable : true
%ANSWER X = factory_isAlwaysCutOnBacktrack : false
%ANSWER X = actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
%ANSWER X = actual_isRetryable : true
%ANSWER X = actual_isAlwaysCutOnBacktrack : false
%ANSWER X = processed_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$NotIndexablePredicateFactory
%ANSWER X = processed_isRetryable : true
%ANSWER X = processed_isAlwaysCutOnBacktrack : false

%QUERY p(c, _)
%ANSWER/
%ANSWER/
%ANSWER/

%QUERY meta_data(p(c, Y), X)
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
% X = processed_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$SingleIndexPredicateFactory
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

%QUERY p(c, Y)
%ANSWER Y=2
%ANSWER Y=3
%ANSWER Y=3

%QUERY meta_data(p(_, 2), X)
%ANSWER X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X = factory_isRetryable : true
%ANSWER X = factory_isAlwaysCutOnBacktrack : false
%ANSWER X = actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
%ANSWER X = actual_isRetryable : true
%ANSWER X = actual_isAlwaysCutOnBacktrack : false
%ANSWER X = processed_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$NotIndexablePredicateFactory
%ANSWER X = processed_isRetryable : true
%ANSWER X = processed_isAlwaysCutOnBacktrack : false

%QUERY p(_, 2)
%ANSWER/
%ANSWER/
%ANSWER/

%QUERY meta_data(p(Y, 2), X)
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

%QUERY p(X, 2)
%ANSWER X=b
%ANSWER X=b
%ANSWER X=c

%QUERY meta_data(p(z, _), X)
%ANSWER X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X = factory_isRetryable : true
%ANSWER X = factory_isAlwaysCutOnBacktrack : false
%ANSWER X = actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
%ANSWER X = actual_isRetryable : true
%ANSWER X = actual_isAlwaysCutOnBacktrack : false
%ANSWER X = processed_class : org.projog.core.predicate.udp.NeverSucceedsPredicateFactory
%ANSWER X = processed_isRetryable : false
%ANSWER X = processed_isAlwaysCutOnBacktrack : false

%FALSE p(z, X)

%QUERY meta_data(p(_, 9), X)
%ANSWER X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X = factory_isRetryable : true
%ANSWER X = factory_isAlwaysCutOnBacktrack : false
%ANSWER X = actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
%ANSWER X = actual_isRetryable : true
%ANSWER X = actual_isAlwaysCutOnBacktrack : false
%ANSWER X = processed_class : org.projog.core.predicate.udp.NeverSucceedsPredicateFactory
%ANSWER X = processed_isRetryable : false
%ANSWER X = processed_isAlwaysCutOnBacktrack : false

%FALSE p(X, 9)

%QUERY meta_data(p(a, 2), X)
%ANSWER X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X = factory_isRetryable : true
%ANSWER X = factory_isAlwaysCutOnBacktrack : false
%ANSWER X = actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
%ANSWER X = actual_isRetryable : true
%ANSWER X = actual_isAlwaysCutOnBacktrack : false
%ANSWER X = processed_class : org.projog.core.predicate.udp.NeverSucceedsPredicateFactory
%ANSWER X = processed_isRetryable : false
%ANSWER X = processed_isAlwaysCutOnBacktrack : false

%FALSE p(a, 2)

%QUERY meta_data(p(e(_), _), X)
%ANSWER X = factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
%ANSWER X = factory_isRetryable : true
%ANSWER X = factory_isAlwaysCutOnBacktrack : false
%ANSWER X = actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
%ANSWER X = actual_isRetryable : true
%ANSWER X = actual_isAlwaysCutOnBacktrack : false
%ANSWER X = processed_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$LinkedHashMapPredicateFactory
%ANSWER X = processed_isRetryable : true
%ANSWER X = processed_isAlwaysCutOnBacktrack : false

%QUERY p(e(_), _)
%ANSWER/
%ANSWER/
%ANSWER/

%QUERY meta_data(p(e(Y), Z), X)
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

%QUERY p(e(X), Y)
%ANSWER
% X=1
% Y=5
%ANSWER
%ANSWER
% X=2
% Y=6
%ANSWER
%ANSWER
% X=3
% Y=7
%ANSWER

