package org.projog.core.udp;

import static org.projog.core.KnowledgeBaseUtils.getProjogProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.projog.core.KnowledgeBase;
import org.projog.core.KnowledgeBaseUtils;
import org.projog.core.Predicate;
import org.projog.core.PredicateFactory;
import org.projog.core.PredicateKey;
import org.projog.core.ProjogException;
import org.projog.core.ProjogProperties;
import org.projog.core.SpyPoints;
import org.projog.core.function.bool.True;
import org.projog.core.function.flow.RepeatSetAmount;
import org.projog.core.term.Term;
import org.projog.core.udp.compiler.CompiledPredicateClassGenerator;
import org.projog.core.udp.interpreter.AlwaysMatchedClauseAction;
import org.projog.core.udp.interpreter.ClauseAction;
import org.projog.core.udp.interpreter.ClauseActionFactory;
import org.projog.core.udp.interpreter.ImmutableArgumentsClauseAction;
import org.projog.core.udp.interpreter.InterpretedTailRecursivePredicateFactory;
import org.projog.core.udp.interpreter.InterpretedUserDefinedPredicate;

/**
 * Maintains a record of the clauses that represents a "static" user defined predicate.
 * <p>
 * A "static" user defined predicate is one that can not have clauses added or removed after it is first defined.
 */
public class StaticUserDefinedPredicateFactory implements UserDefinedPredicateFactory {
   private final Object lock = new Object();
   private final PredicateKey predicateKey;
   private final List<ClauseModel> implications;
   private KnowledgeBase kb;
   private PredicateFactory compiledPredicateFactory;

   public StaticUserDefinedPredicateFactory(PredicateKey predicateKey) {
      this.predicateKey = predicateKey;
      this.implications = new ArrayList<>();
   }

   @Override
   public void setKnowledgeBase(KnowledgeBase kb) {
      this.kb = kb;
   }

   /**
    * Not supported.
    * <p>
    * It is not possible to add a clause to the beginning of a <i>static</i> user defined predicate.
    * 
    * @throws UnsupportedOperationException
    */
   @Override
   public void addFirst(ClauseModel clauseModel) {
      throw new UnsupportedOperationException();
   }

   /**
    * Adds new clause to list of clauses for this predicate.
    * <p>
    * Note: it is not possible to add clauses to a <i>static</i> user defined predicate once it has been compiled.
    * 
    * @throws IllegalStateException if the predicate has already been compiled.
    */
   @Override
   public void addLast(ClauseModel clauseModel) {
      if (compiledPredicateFactory == null) {
         implications.add(clauseModel);
      } else {
         throw new IllegalStateException(predicateKey + " already compiled so cannot add: " + clauseModel);
      }
   }

   public void compile() {
      // make sure we only call setCompiledPredicateFactory once per instance
      if (compiledPredicateFactory == null) {
         synchronized (lock) {
            if (compiledPredicateFactory == null) {
               setCompiledPredicateFactory();
            }
         }
      }
   }

   private void setCompiledPredicateFactory() {
      final List<ClauseAction> rows = createClauseActionsFromClauseModels();

      if (isAllAlwaysMatchedClauseActions(rows)) {
         compiledPredicateFactory = createPredicateFactoryFromAlwaysMatchedClauseActions(rows);
      } else if (isAllImmutableArgumentsClauseActions(rows)) {
         compiledPredicateFactory = createPredicateFactoryFromNoVariableArgumentsClauseActions(rows);
      } else {
         compiledPredicateFactory = createPredicateFactoryFromClauseActions(rows);
      }
   }

   private List<ClauseAction> createClauseActionsFromClauseModels() {
      final List<ClauseAction> rows = new ArrayList<>(implications.size());
      for (ClauseModel clauseModel : implications) {
         ClauseAction row = ClauseActionFactory.getClauseAction(kb, clauseModel);
         rows.add(row);
      }
      return rows;
   }

   private boolean isAllImmutableArgumentsClauseActions(List<ClauseAction> rows) {
      for (ClauseAction r : rows) {
         if ((r instanceof ImmutableArgumentsClauseAction) == false) {
            return false;
         }
      }
      return true;
   }

   private boolean isAllAlwaysMatchedClauseActions(List<ClauseAction> rows) {
      for (ClauseAction r : rows) {
         if ((r instanceof AlwaysMatchedClauseAction) == false) {
            return false;
         }
      }
      return true;
   }

   private PredicateFactory createPredicateFactoryFromAlwaysMatchedClauseActions(List<ClauseAction> rows) {
      if (rows.size() == 1) {
         // if single row e.g. "a" then just return instance of True (see Disjunction test for an example) 
         return new True();
      } else {
         // e.g. a. a. a. or p(_). p(_). p(_).
         return new RepeatSetAmount(rows.size());
      }
   }

   private PredicateFactory createPredicateFactoryFromNoVariableArgumentsClauseActions(List<ClauseAction> rows) {
      if (predicateKey.getNumArgs() == 1) {
         Term data[] = createSingleDimensionTermArrayOfImplications();
         if (data.length == 1) {
            return new SingleRuleWithSingleImmutableArgumentPredicate(data[0], getSpyPoint());
         } else {
            return new MultipleRulesWithSingleImmutableArgumentPredicate(data, getSpyPoint());
         }
      } else {
         Term data[][] = createTwoDimensionTermArrayOfImplications();
         if (data.length == 1) {
            return new SingleRuleWithMultipleImmutableArgumentsPredicate(data[0], getSpyPoint());
         } else {
            return new MultipleRulesWithMultipleImmutableArgumentsPredicate(data, getSpyPoint());
         }
      }
   }

   private Term[] createSingleDimensionTermArrayOfImplications() {
      Term data[] = new Term[implications.size()];
      for (int i = 0; i < implications.size(); i++) {
         Term arg = implications.get(i).getConsequent().getArgs()[0];
         data[i] = arg;
      }
      return data;
   }

   private Term[][] createTwoDimensionTermArrayOfImplications() {
      int numArgs = predicateKey.getNumArgs();
      Term data[][] = new Term[implications.size()][numArgs];
      for (int i = 0; i < implications.size(); i++) {
         Term[] args = implications.get(i).getConsequent().getArgs();
         data[i] = args;
      }
      return data;
   }

   private PredicateFactory createPredicateFactoryFromClauseActions(List<ClauseAction> clauseActions) {
      List<ClauseModel> clauseModels = getCopyOfImplications();

      if (getProperties().isRuntimeCompilationEnabled() && isPredicateSuitableForCompilation(clauseModels)) {
         return CompiledPredicateClassGenerator.generateCompiledPredicate(kb, clauseModels);
      }

      TailRecursivePredicateMetaData tailRecursiveMetaData = TailRecursivePredicateMetaData.create(kb, clauseModels);
      if (tailRecursiveMetaData != null) {
         return new InterpretedTailRecursivePredicateFactory(kb, tailRecursiveMetaData);
      }

      return new InterpretedUserDefinedPredicatePredicateFactory(predicateKey, getSpyPoint(), clauseActions);
   }

   private List<ClauseModel> getCopyOfImplications() {
      List<ClauseModel> copyImplications = new ArrayList<>(implications.size());
      for (ClauseModel clauseModel : implications) {
         copyImplications.add(clauseModel.copy());
      }
      return copyImplications;
   }

   private boolean isPredicateSuitableForCompilation(List<ClauseModel> clauseModels) {
      for (ClauseModel cm : clauseModels) {
         Term antecedant = cm.getAntecedant();
         if (!isTermSuitableForCompilation(antecedant)) {
            return false;
         }
      }
      return true;
   }

   private boolean isTermSuitableForCompilation(Term antecedant) {
      if (antecedant.getType().isVariable()) {
         return false;
      } else if (KnowledgeBaseUtils.isConjunction(antecedant)) {
         for (Term t : KnowledgeBaseUtils.toArrayOfConjunctions(antecedant)) {
            if (t.getType().isVariable()) {
               return false;
            }
         }
      }
      return true;
   }

   private SpyPoints.SpyPoint getSpyPoint() {
      if (getProperties().isSpyPointsEnabled()) {
         return kb.getSpyPoints().getSpyPoint(predicateKey);
      } else {
         return null;
      }
   }

   private ProjogProperties getProperties() {
      return getProjogProperties(kb);
   }

   @Override
   public Predicate getPredicate(Term... args) {
      if (args.length != predicateKey.getNumArgs()) {
         throw new ProjogException("User defined predicate: " + predicateKey + " is being called with the wrong number of arguments: " + args.length + " " + Arrays.toString(args));
      }
      compile();
      return compiledPredicateFactory.getPredicate(args);
   }

   @Override
   public PredicateKey getPredicateKey() {
      return predicateKey;
   }

   public PredicateFactory getActualPredicateFactory() {
      compile();
      return compiledPredicateFactory;
   }

   /**
    * Returns an iterator over the clauses of this user defined predicate.
    * <p>
    * The iterator returned will have the following characteristics which prevent the underlying structure of the user
    * defined predicate being altered:
    * <ul>
    * <li>Calls to {@link java.util.Iterator#next()} return a <i>new copy</i> of the {@link ClauseModel}.</li>
    * <li>Calls to {@link java.util.Iterator#remove()} cause a {@code UnsupportedOperationException}</li>
    * <li>
    * </ul>
    */
   @Override
   public Iterator<ClauseModel> getImplications() {
      return new ImplicationsIterator(implications);
   }

   @Override
   public boolean isDynamic() {
      return false;
   }

   @Override
   public ClauseModel getClauseModel(int index) {
      if (index >= implications.size()) {
         return null;
      }
      return implications.get(index).copy();
   }

   /**
    * @see StaticUserDefinedPredicateFactory#getImplications
    */
   private static class ImplicationsIterator implements Iterator<ClauseModel> {
      private final Iterator<ClauseModel> iterator;

      ImplicationsIterator(List<ClauseModel> implications) {
         iterator = implications.iterator();
      }

      @Override
      public boolean hasNext() {
         return iterator.hasNext();
      }

      /**
       * Returns a <i>new copy</i> to avoid the original being altered.
       */
      @Override
      public ClauseModel next() {
         ClauseModel clauseModel = iterator.next();
         return clauseModel.copy();
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }
   }

   private static class InterpretedUserDefinedPredicatePredicateFactory implements PredicateFactory {
      private final PredicateKey key;
      private final List<ClauseAction> rows;
      private final SpyPoints.SpyPoint spyPoint;

      InterpretedUserDefinedPredicatePredicateFactory(PredicateKey key, SpyPoints.SpyPoint spyPoint, List<ClauseAction> rows) {
         this.key = key;
         this.spyPoint = spyPoint;
         this.rows = rows;
      }

      @Override
      public Predicate getPredicate(Term... args) {
         return new InterpretedUserDefinedPredicate(key, spyPoint, rows.iterator());
      }

      @Override
      public void setKnowledgeBase(KnowledgeBase kb) {
      }
   }
}