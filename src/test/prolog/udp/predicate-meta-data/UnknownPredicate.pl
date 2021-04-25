?- pj_add_predicate(meta_data/2, 'org.projog.core.predicate.udp.PredicateMetaData').

%QUERY meta_data(p(_), X)
%ANSWER X = factory_class : org.projog.core.predicate.UnknownPredicate
%ANSWER X = factory_isRetryable : true
%ANSWER X = factory_isAlwaysCutOnBacktrack : false
%ANSWER X = processed_class : org.projog.core.predicate.UnknownPredicate
%ANSWER X = processed_isRetryable : true
%ANSWER X = processed_isAlwaysCutOnBacktrack : false

%FALSE p(a)

%TRUE assert(p(a)), assert(p(b)), assert(p(c))

%QUERY meta_data(p(_), X)
%ANSWER X = factory_class : org.projog.core.predicate.udp.DynamicUserDefinedPredicateFactory
%ANSWER X = factory_isRetryable : true
%ANSWER X = factory_isAlwaysCutOnBacktrack : false

%TRUE p(a)

