package org.projog.core.function.io;

import org.projog.core.function.AbstractSingletonPredicate;

/* TEST
 %QUERY write('a'), write('b'), nl, write('c')
 %OUTPUT
 % ab
 % c
 %OUTPUT
 %ANSWER/
 */
/**
 * <code>nl</code> - outputs a new line character.
 * <p>
 * Causes a line break to be output to the current stream.
 * </p>
 * <p>
 * This goal succeeds only once.
 * </p>
 */
public final class NewLine extends AbstractSingletonPredicate {
   @Override
   public boolean evaluate() {
      getKnowledgeBase().getFileHandles().getCurrentOutputStream().println();
      return true;
   }
}