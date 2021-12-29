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

%?- meta_data(p(_, _), X)
% X=factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
% X=factory_isRetryable : true
% X=factory_isAlwaysCutOnBacktrack : false
% X=actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% X=actual_isRetryable : true
% X=actual_isAlwaysCutOnBacktrack : false
% X=processed_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% X=processed_isRetryable : true
% X=processed_isAlwaysCutOnBacktrack : false

%?- p(X,Y)
% X=a
% Y=1
% X=b
% Y=2
% X=b
% Y=2
% X=c
% Y=2
% X=c
% Y=3
% X=c
% Y=3
% X=d
% Y=4
% X=d
% Y=4
% X=e(1)
% Y=5
% X=e(2)
% Y=6
% X=e(3)
% Y=7
% X=f
% Y=8

%?- meta_data(p(a, _), X)
% X=factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
% X=factory_isRetryable : true
% X=factory_isAlwaysCutOnBacktrack : false
% X=actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% X=actual_isRetryable : true
% X=actual_isAlwaysCutOnBacktrack : false
% X=processed_class : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory
% X=processed_isRetryable : false
% X=processed_isAlwaysCutOnBacktrack : false

%?- p(a, X)
% X=1

%?- meta_data(p(_, 1), X)
% X=factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
% X=factory_isRetryable : true
% X=factory_isAlwaysCutOnBacktrack : false
% X=actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% X=actual_isRetryable : true
% X=actual_isAlwaysCutOnBacktrack : false
% X=processed_class : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory
% X=processed_isRetryable : false
% X=processed_isAlwaysCutOnBacktrack : false

%?- p(X, 1)
% X=a

%?- meta_data(p(b, _), X)
% X=factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
% X=factory_isRetryable : true
% X=factory_isAlwaysCutOnBacktrack : false
% X=actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% X=actual_isRetryable : true
% X=actual_isAlwaysCutOnBacktrack : false
% X=processed_class : org.projog.core.predicate.udp.SingleRetryableRulePredicateFactory
% X=processed_isRetryable : true
% X=processed_isAlwaysCutOnBacktrack : false

%?- p(b, X)
% X=2
% X=2

%?- meta_data(p(_, 4), X)
% X=factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
% X=factory_isRetryable : true
% X=factory_isAlwaysCutOnBacktrack : false
% X=actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% X=actual_isRetryable : true
% X=actual_isAlwaysCutOnBacktrack : false
% X=processed_class : org.projog.core.predicate.udp.SingleRetryableRulePredicateFactory
% X=processed_isRetryable : true
% X=processed_isAlwaysCutOnBacktrack : false

%?- p(X, 4)
% X=d
% X=d

%?- meta_data(p(c, _), X)
% X=factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
% X=factory_isRetryable : true
% X=factory_isAlwaysCutOnBacktrack : false
% X=actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% X=actual_isRetryable : true
% X=actual_isAlwaysCutOnBacktrack : false
% X=processed_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$NotIndexablePredicateFactory
% X=processed_isRetryable : true
% X=processed_isAlwaysCutOnBacktrack : false

%?- p(c, _)
%YES
%YES
%YES

%?- meta_data(p(c, Y), X)
% X=factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
% Y=UNINSTANTIATED VARIABLE
% X=factory_isRetryable : true
% Y=UNINSTANTIATED VARIABLE
% X=factory_isAlwaysCutOnBacktrack : false
% Y=UNINSTANTIATED VARIABLE
% X=actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% Y=UNINSTANTIATED VARIABLE
% X=actual_isRetryable : true
% Y=UNINSTANTIATED VARIABLE
% X=actual_isAlwaysCutOnBacktrack : false
% Y=UNINSTANTIATED VARIABLE
% X=processed_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$SingleIndexPredicateFactory
% Y=UNINSTANTIATED VARIABLE
% X=processed_isRetryable : true
% Y=UNINSTANTIATED VARIABLE
% X=processed_isAlwaysCutOnBacktrack : false
% Y=UNINSTANTIATED VARIABLE

%?- p(c, Y)
% Y=2
% Y=3
% Y=3

%?- meta_data(p(_, 2), X)
% X=factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
% X=factory_isRetryable : true
% X=factory_isAlwaysCutOnBacktrack : false
% X=actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% X=actual_isRetryable : true
% X=actual_isAlwaysCutOnBacktrack : false
% X=processed_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$NotIndexablePredicateFactory
% X=processed_isRetryable : true
% X=processed_isAlwaysCutOnBacktrack : false

%?- p(_, 2)
%YES
%YES
%YES

%?- meta_data(p(Y, 2), X)
% X=factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
% Y=UNINSTANTIATED VARIABLE
% X=factory_isRetryable : true
% Y=UNINSTANTIATED VARIABLE
% X=factory_isAlwaysCutOnBacktrack : false
% Y=UNINSTANTIATED VARIABLE
% X=actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% Y=UNINSTANTIATED VARIABLE
% X=actual_isRetryable : true
% Y=UNINSTANTIATED VARIABLE
% X=actual_isAlwaysCutOnBacktrack : false
% Y=UNINSTANTIATED VARIABLE
% X=processed_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$LinkedHashMapPredicateFactory
% Y=UNINSTANTIATED VARIABLE
% X=processed_isRetryable : true
% Y=UNINSTANTIATED VARIABLE
% X=processed_isAlwaysCutOnBacktrack : false
% Y=UNINSTANTIATED VARIABLE

%?- p(X, 2)
% X=b
% X=b
% X=c

%?- meta_data(p(z, _), X)
% X=factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
% X=factory_isRetryable : true
% X=factory_isAlwaysCutOnBacktrack : false
% X=actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% X=actual_isRetryable : true
% X=actual_isAlwaysCutOnBacktrack : false
% X=processed_class : org.projog.core.predicate.udp.NeverSucceedsPredicateFactory
% X=processed_isRetryable : false
% X=processed_isAlwaysCutOnBacktrack : false

%FAIL p(z, X)

%?- meta_data(p(_, 9), X)
% X=factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
% X=factory_isRetryable : true
% X=factory_isAlwaysCutOnBacktrack : false
% X=actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% X=actual_isRetryable : true
% X=actual_isAlwaysCutOnBacktrack : false
% X=processed_class : org.projog.core.predicate.udp.NeverSucceedsPredicateFactory
% X=processed_isRetryable : false
% X=processed_isAlwaysCutOnBacktrack : false

%FAIL p(X, 9)

%?- meta_data(p(a, 2), X)
% X=factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
% X=factory_isRetryable : true
% X=factory_isAlwaysCutOnBacktrack : false
% X=actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% X=actual_isRetryable : true
% X=actual_isAlwaysCutOnBacktrack : false
% X=processed_class : org.projog.core.predicate.udp.NeverSucceedsPredicateFactory
% X=processed_isRetryable : false
% X=processed_isAlwaysCutOnBacktrack : false

%FAIL p(a, 2)

%?- meta_data(p(e(_), _), X)
% X=factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
% X=factory_isRetryable : true
% X=factory_isAlwaysCutOnBacktrack : false
% X=actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% X=actual_isRetryable : true
% X=actual_isAlwaysCutOnBacktrack : false
% X=processed_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$LinkedHashMapPredicateFactory
% X=processed_isRetryable : true
% X=processed_isAlwaysCutOnBacktrack : false

%?- p(e(_), _)
%YES
%YES
%YES

%?- meta_data(p(e(Y), Z), X)
% X=factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
% Y=UNINSTANTIATED VARIABLE
% Z=UNINSTANTIATED VARIABLE
% X=factory_isRetryable : true
% Y=UNINSTANTIATED VARIABLE
% Z=UNINSTANTIATED VARIABLE
% X=factory_isAlwaysCutOnBacktrack : false
% Y=UNINSTANTIATED VARIABLE
% Z=UNINSTANTIATED VARIABLE
% X=actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% Y=UNINSTANTIATED VARIABLE
% Z=UNINSTANTIATED VARIABLE
% X=actual_isRetryable : true
% Y=UNINSTANTIATED VARIABLE
% Z=UNINSTANTIATED VARIABLE
% X=actual_isAlwaysCutOnBacktrack : false
% Y=UNINSTANTIATED VARIABLE
% Z=UNINSTANTIATED VARIABLE
% X=processed_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% Y=UNINSTANTIATED VARIABLE
% Z=UNINSTANTIATED VARIABLE
% X=processed_isRetryable : true
% Y=UNINSTANTIATED VARIABLE
% Z=UNINSTANTIATED VARIABLE
% X=processed_isAlwaysCutOnBacktrack : false
% Y=UNINSTANTIATED VARIABLE
% Z=UNINSTANTIATED VARIABLE

%?- p(e(X), Y)
% X=1
% Y=5
% X=2
% Y=6
% X=3
% Y=7

