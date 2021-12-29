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
%?- meta_data(p(a, 1, _), X)
% X=factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
% X=factory_isRetryable : true
% X=factory_isAlwaysCutOnBacktrack : false
% X=actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% X=actual_isRetryable : true
% X=actual_isAlwaysCutOnBacktrack : false
% X=processed_class : org.projog.core.predicate.udp.SingleRetryableRulePredicateFactory
% X=processed_isRetryable : true
% X=processed_isAlwaysCutOnBacktrack : false

% SingleRetryableRulePredicateFactory retryable, has cut
%?- meta_data(p(a, 2, _), X)
% X=factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
% X=factory_isRetryable : true
% X=factory_isAlwaysCutOnBacktrack : false
% X=actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% X=actual_isRetryable : true
% X=actual_isAlwaysCutOnBacktrack : false
% X=processed_class : org.projog.core.predicate.udp.SingleRetryableRulePredicateFactory
% X=processed_isRetryable : true
% X=processed_isAlwaysCutOnBacktrack : false

% SingleNonRetryableRulePredicateFactory - non retryable, has cut
%?- meta_data(p(a, 3, _), X)
% X=factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
% X=factory_isRetryable : true
% X=factory_isAlwaysCutOnBacktrack : false
% X=actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% X=actual_isRetryable : true
% X=actual_isAlwaysCutOnBacktrack : false
% X=processed_class : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory
% X=processed_isRetryable : false
% X=processed_isAlwaysCutOnBacktrack : false

% SingleNonRetryableRulePredicateFactory - non retryable, no cut
%?- meta_data(p(a, 4, _), X)
% X=factory_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory
% X=factory_isRetryable : true
% X=factory_isAlwaysCutOnBacktrack : false
% X=actual_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$IndexablePredicateFactory
% X=actual_isRetryable : true
% X=actual_isAlwaysCutOnBacktrack : false
% X=processed_class : org.projog.core.predicate.udp.SingleNonRetryableRulePredicateFactory
% X=processed_isRetryable : false
% X=processed_isAlwaysCutOnBacktrack : false

% IndexablePredicateFactory - retryable
%?- meta_data(p(f, Y, Z), X)
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

% IndexablePredicateFactory not retryable
%?- meta_data(p(g, Y, Z), X)
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
% X=processed_isRetryable : false
% Y=UNINSTANTIATED VARIABLE
% Z=UNINSTANTIATED VARIABLE
% X=processed_isAlwaysCutOnBacktrack : false
% Y=UNINSTANTIATED VARIABLE
% Z=UNINSTANTIATED VARIABLE

% SingleIndexPredicateFactory retryable
%?- meta_data(p(b, Y, Z), X)
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
% X=processed_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$SingleIndexPredicateFactory
% Y=UNINSTANTIATED VARIABLE
% Z=UNINSTANTIATED VARIABLE
% X=processed_isRetryable : true
% Y=UNINSTANTIATED VARIABLE
% Z=UNINSTANTIATED VARIABLE
% X=processed_isAlwaysCutOnBacktrack : false
% Y=UNINSTANTIATED VARIABLE
% Z=UNINSTANTIATED VARIABLE

% SingleIndexPredicateFactory not retryable
%?- meta_data(p(d, Y, Z), X)
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
% X=processed_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$SingleIndexPredicateFactory
% Y=UNINSTANTIATED VARIABLE
% Z=UNINSTANTIATED VARIABLE
% X=processed_isRetryable : false
% Y=UNINSTANTIATED VARIABLE
% Z=UNINSTANTIATED VARIABLE
% X=processed_isAlwaysCutOnBacktrack : false
% Y=UNINSTANTIATED VARIABLE
% Z=UNINSTANTIATED VARIABLE

% LinkedHashMapPredicateFactory retryable
%?- meta_data(p(c, 3, Y), X)
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

% LinkedHashMapPredicateFactory not retryable
%?- meta_data(p(c, 4, Y), X)
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
% X=processed_isRetryable : false
% Y=UNINSTANTIATED VARIABLE
% X=processed_isAlwaysCutOnBacktrack : false
% Y=UNINSTANTIATED VARIABLE

% NotIndexablePredicateFactory retryable
%?- meta_data(p(e, 2, Y), X)
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
% X=processed_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$NotIndexablePredicateFactory
% Y=UNINSTANTIATED VARIABLE
% X=processed_isRetryable : true
% Y=UNINSTANTIATED VARIABLE
% X=processed_isAlwaysCutOnBacktrack : false
% Y=UNINSTANTIATED VARIABLE

% NotIndexablePredicateFactory not retryable
%?- meta_data(p(b, 2, Y), X)
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
% X=processed_class : org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory$NotIndexablePredicateFactory
% Y=UNINSTANTIATED VARIABLE
% X=processed_isRetryable : false
% Y=UNINSTANTIATED VARIABLE
% X=processed_isAlwaysCutOnBacktrack : false
% Y=UNINSTANTIATED VARIABLE

