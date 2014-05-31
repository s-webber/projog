package org.projog.core;

import org.projog.core.term.Numeric;
import org.projog.core.term.Term;

/**
 * Represents an arithmetic expression.
 * <p>
 * <img src="doc-files/Calculatable.png">
 * 
 * @see org.projog.core.Calculatables
 */
public interface Calculatable {
   /**
    * Returns the result of the calculation using the specified arguments.
    * 
    * @param args the arguments to use in the calculation
    * @return the result of the calculation using the specified arguments
    */
   Numeric calculate(KnowledgeBase kb, Term[] args);
}