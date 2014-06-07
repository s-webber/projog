package org.projog.core.term;

import static org.junit.Assert.assertEquals;
import static org.projog.TestUtils.createKnowledgeBase;
import static org.projog.TestUtils.parseSentence;

import org.junit.Test;
import org.projog.core.Operands;

public class TermFormatterTest {
   @Test
   public void testTermToString() {
      String inputSyntax = "?- X = -1 + 1.684 , p(1, 7.3, [_,[]|c])";
      Term inputTerm = parseSentence(inputSyntax + ".");

      TermFormatter tf = createFormatter();
      assertEquals(inputSyntax, tf.toString(inputTerm));
   }

   private TermFormatter createFormatter() {
      Operands operands = createKnowledgeBase().getOperands();
      return new TermFormatter(operands);
   }
}
