package org.projog.core.function.io;

import static org.projog.core.KnowledgeBaseUtils.getOperands;

import java.io.InputStreamReader;

import org.projog.core.Operands;
import org.projog.core.function.AbstractSingletonPredicate;
import org.projog.core.parser.SentenceParser;
import org.projog.core.term.Term;

/* TEST
 %LINK prolog-io
 */
/**
 * <code>read(X)</code> - reads a term from the input stream.
 * <p>
 * <code>read(X)</code> reads the next term from the input stream and matches it with <code>X</code>.
 * </p>
 * <p>
 * Succeeds only once.
 * </p>
 */
public final class Read extends AbstractSingletonPredicate {
   private Operands operands;

   @Override
   protected void init() {
      operands = getOperands(getKnowledgeBase());
   }

   @Override
   public boolean evaluate(Term argument) {
      InputStreamReader isr = new InputStreamReader(getKnowledgeBase().getFileHandles().getCurrentInputStream());
      SentenceParser sp = SentenceParser.getInstance(isr, operands);
      Term t = sp.parseTerm();
      return argument.unify(t);
   }
}