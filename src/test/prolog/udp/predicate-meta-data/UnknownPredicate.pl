?- pj_add_predicate(meta_data/2, 'org.projog.core.predicate.udp.PredicateMetaData').

%?- meta_data(p(_), X)
% X=factory_class : org.projog.core.predicate.UnknownPredicate
% X=factory_isRetryable : true
% X=factory_isAlwaysCutOnBacktrack : false
% X=processed_class : org.projog.core.predicate.UnknownPredicate
% X=processed_isRetryable : true
% X=processed_isAlwaysCutOnBacktrack : false

%FAIL p(a)

%TRUE assert(p(a)), assert(p(b)), assert(p(c))

%?- meta_data(p(_), X)
% X=factory_class : org.projog.core.predicate.udp.DynamicUserDefinedPredicateFactory
% X=factory_isRetryable : true
% X=factory_isAlwaysCutOnBacktrack : false

%TRUE p(a)

