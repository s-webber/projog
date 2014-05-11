package org.projog.core;

import static org.projog.core.term.TermUtils.castToNumeric;
import static org.projog.core.term.TermUtils.getAtomName;

import org.projog.core.term.Term;
import org.projog.core.term.TermType;

/**
 * Represents the structure of a {@link Term}.
 * <p>
 * Defines {@link Term}s by their name (functor) and number of arguments (arity). This "metadata" or
 * "descriptor information" allows rules whose heads (consequences) share the same structure to be grouped together.
 * <p>
 * As {@link org.projog.core.term.Atom} and {@link org.projog.core.term.Structure} are the only subclasses of
 * {@link org.projog.core.term.Term} that can be the head (consequent) of a rule they are the only subclasses of
 * {@link Term} that {@code PredicateKey} is intended to describe.
 * <p>
 * PredicateKeys are constant; their values cannot be changed after they are created.
 */
public final class PredicateKey implements Comparable<PredicateKey> {
   private final String name;
   private final int numArgs;

   /**
    * Returns a {@code PredicateKey} for the specified term.
    * 
    * @param t a term the returned {@code PredicateKey} should represent (needs to have a {@link Term#getType()} value
    * of {@link TermType#ATOM} or {@link TermType#STRUCTURE})
    * @return a {@code PredicateKey} for the specified term.
    * @throws ProjogException if {@code t} is not of type {@link TermType#ATOM} or {@link TermType#STRUCTURE}
    */
   public static PredicateKey createForTerm(Term t) {
      int numArgs;
      switch (t.getType()) {
         case ATOM:
            numArgs = -1;
            break;
         case STRUCTURE:
            numArgs = t.getArgs().length;
            break;
         default:
            throw new ProjogException(getInvalidTypeExceptionMessage(t));
      }
      return new PredicateKey(t.getName(), numArgs);
   }

   /**
    * @param t must be either an atom representing the predicate name, or a structure named {@code /} where the first
    * argument is the name of the predicate to represent and the second (and final) argument is the arity.
    */
   public static PredicateKey createFromNameAndArity(Term t) {
      if (t.getType() == TermType.ATOM) {
         return createForTerm(t);
      }

      if (t.getType() != TermType.STRUCTURE) {
         throw new ProjogException(getInvalidTypeExceptionMessage(t));
      }

      if (!"/".equals(t.getName()) || t.getArgs().length != 2) {
         throw new ProjogException("Expected a predicate with two arguments and the name: '/' but got: " + t);
      }

      String name = getAtomName(t.getArgs()[0]);
      int arity = castToNumeric(t.getArgs()[1]).getInt();
      return new PredicateKey(name, arity);
   }

   private static String getInvalidTypeExceptionMessage(Term t) {
      return "Expected an atom or a predicate but got a " + t.getType() + " with value: " + t;
   }

   public PredicateKey(String name, int numArgs) {
      this.name = name;
      this.numArgs = numArgs;
   }

   public String getName() {
      return name;
   }

   public int getNumArgs() {
      return numArgs;
   }

   /**
    * @param o the reference object with which to compare.
    * @return {@code true} if {@code o} is an instanceof {@code PredicateKey} and has the same name and number of
    * arguments as this instance
    */
   @Override
   public boolean equals(Object o) {
      if (o instanceof PredicateKey) {
         PredicateKey k = (PredicateKey) o;
         return name.equals(k.name) && numArgs == k.numArgs;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return name.hashCode() + numArgs;
   }

   /**
    * @return {@code name+"/"+numArgs}
    */
   @Override
   public String toString() {
      return name + "/" + numArgs;
   }

   /**
    * Ordered on name or, if names identical, number of arguments.
    */
   @Override
   public int compareTo(PredicateKey o) {
      int c = name.compareTo(o.name);
      if (c == 0) {
         return Integer.compare(numArgs, o.numArgs);
      } else {
         return c;
      }
   }
}