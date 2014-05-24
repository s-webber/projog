package org.projog.core.udp.compiler;

import static org.projog.core.udp.compiler.CompiledPredicateSourceGeneratorUtils.getUnifyStatement;

import java.util.HashMap;
import java.util.Map;

import org.projog.core.term.Numeric;
import org.projog.core.term.Term;
import org.projog.core.term.TermType;
import org.projog.core.term.TermUtils;

/**
 * Outputs java code that matches functionality of {@link org.projog.core.function.math.Is}
 */
final class IsPredicateInvocationGenerator implements PredicateInvocationGenerator {
   /**
    * Contains a collection of standard mathematical operators.
    * <p>
    * Maps from Prolog version (key) to Java version (value).
    */
   private static final Map<String, String> ops = new HashMap<>();
   static {
      ops.put("+", "+");
      ops.put("-", "-");
      ops.put("*", "*");
      ops.put("mod", "%");
   }

   @Override
   public void generate(CompiledPredicateWriter g) {
      Term function = g.currentClause().getCurrentFunction();

      // only add arg1 variables to currentClause.variablesToBackTrack as arg2's will not be updated
      Term arg1 = function.getArgument(0);
      g.currentClause().addVariablesToBackTrack(TermUtils.getAllVariablesInTerm(arg1));

      Term arg2 = function.getArgument(1);

      String numeric = getCalculatableExpression(g, arg2);

      if (arg1.getType() == TermType.NAMED_VARIABLE && g.declareVariableIfNotAlready(arg1, false)) {
         String variableId = g.getVariableId(arg1);
         g.classVariables().addAssignedVariable(variableId);
         g.assign(variableId, numeric);
      } else {
         String variableId = g.outputCreateTermStatement(arg1, true);
         String eval = "!" + getUnifyStatement(variableId, numeric); // reuse unify statement
         g.outputIfTrueThenBreak(eval);
      }
   }

   // TODO method too long - refactor
   private String getCalculatableExpression(CompiledPredicateWriter g, Term t) {
      if (t.isImmutable()) {
         return g.outputCreateTermStatement(g.knowledgeBase().getNumeric(t), true);
      } else if (t.getType() == TermType.STRUCTURE && t.getNumberOfArguments() == 2 && ops.containsKey(t.getName())) {
         String op = ops.get(t.getName());
         Term arg1 = t.getArgument(0);
         Term arg2 = t.getArgument(1);
         boolean isResultDouble = arg1.getType() == TermType.DOUBLE || arg2.getType() == TermType.DOUBLE;
         String string1;
         if (arg1.getType().isVariable() == false && arg1.getType().isNumeric() == false) {
            string1 = "num" + g.currentClause().getNextNumericIndex();
            g.assign("Numeric " + string1, getNumeric(getCalculatableExpression(g, arg1), g));
         } else {
            string1 = g.outputCreateTermStatement(t.getArgument(0), true);
         }
         String string2;
         if (arg2.getType().isVariable() == false && arg2.getType().isNumeric() == false) {
            string2 = "num" + g.currentClause().getNextNumericIndex();
            g.assign("Numeric " + string2, getNumeric(getCalculatableExpression(g, arg2), g));
         } else {
            string2 = g.outputCreateTermStatement(t.getArgument(1), true);
         }
         StringBuilder s = new StringBuilder();
         String arg1TempNumericPlaceholder = null;
         String arg2TempNumericPlaceholder = null;
         if (isResultDouble == false) {
            s.append("((");
            if (arg1.getType().isNumeric() == false) {
               arg1TempNumericPlaceholder = g.classVariables().getNewTempNumericName();
               g.writeStatement("final Numeric " + arg1TempNumericPlaceholder);
               s.append("(" + arg1TempNumericPlaceholder + "=" + getNumeric(string1, g) + ").getType()==TermType.INTEGER");
            }
            if (arg1.getType().isNumeric() == false && arg2.getType().isNumeric() == false) {
               s.append(" & ");
            }
            if (arg2.getType().isNumeric() == false) {
               arg2TempNumericPlaceholder = g.classVariables().getNewTempNumericName();
               g.writeStatement("final Numeric " + arg2TempNumericPlaceholder);
               s.append("(" + arg2TempNumericPlaceholder + "=" + getNumeric(string2, g) + ").getType()==TermType.INTEGER");
            }
            s.append(")?");
            s.append("new IntegerNumber(");
            if (arg1.getType().isNumeric()) {
               s.append(((Numeric) arg1).getInt());
            } else {
               s.append(arg1TempNumericPlaceholder + ".getInt()");
            }
            s.append(op);
            if (arg2.getType().isNumeric()) {
               s.append(((Numeric) arg2).getInt());
            } else {
               s.append(arg2TempNumericPlaceholder + ".getInt()");
            }
            s.append(")");
            s.append(":");
         }
         s.append("new DoubleNumber(");
         if (arg1.getType().isNumeric()) {
            s.append(((Numeric) arg1).getDouble());
         } else {
            s.append(arg1TempNumericPlaceholder + ".getDouble()");
         }
         s.append(op);
         if (arg2.getType().isNumeric()) {
            s.append(((Numeric) arg2).getDouble());
         } else {
            s.append(arg2TempNumericPlaceholder + ".getDouble()");
         }
         if (isResultDouble == false) {
            s.append(")");
         }
         s.append(")");
         return s.toString();
      } else {
         return getNumeric(g.outputCreateTermStatement(t, true), g);
      }
   }

   private static String getNumeric(final String variableId, final CompiledPredicateWriter g) {
      g.setNeedsKnowledgeBaseStaticVariable(true);
      return "kb.getNumeric(" + variableId + ")";
   }
}