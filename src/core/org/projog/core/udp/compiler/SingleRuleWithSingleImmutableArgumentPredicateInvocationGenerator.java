package org.projog.core.udp.compiler;

import org.projog.core.PredicateFactory;
import org.projog.core.term.Term;
import org.projog.core.udp.SingleRuleWithSingleImmutableArgumentPredicate;

final class SingleRuleWithSingleImmutableArgumentPredicateInvocationGenerator implements PredicateInvocationGenerator {
   @Override
   public void generate(CompiledPredicateWriter g) {
      Term function = g.currentClause().getCurrentFunction();
      PredicateFactory ef = g.currentClause().getCurrentPredicateFactory();

      String functionVariableName = null;
      if (g.isSpyPointsEnabled()) {
         functionVariableName = g.classVariables().getPredicateFactoryVariableName(function, g.knowledgeBase());
         g.logInlinedPredicatePredicate("Call", functionVariableName, function);
      }
      Term data = ((SingleRuleWithSingleImmutableArgumentPredicate) ef).data;
      Runnable r = g.createOnBreakCallback(functionVariableName, function, null);
      g.comment("SingleRuleWithSingleImmutableArgumentPredicateGenerator");
      g.outputEqualsEvaluation(function.getArgument(0), data, r);
      g.logInlinedPredicatePredicate("Exit", functionVariableName, function);
   }
}