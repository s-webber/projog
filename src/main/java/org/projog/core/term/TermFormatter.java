/*
 * Copyright 2013 S. Webber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.projog.core.term;

import org.projog.core.parser.Operands;

/**
 * Produces {@code String} representations of {@link Term} instances.
 * <p>
 * Does take account of operator precedence.
 *
 * @see #formatTerm(Term)
 */
public class TermFormatter {
   private final Operands operands;

   public TermFormatter(Operands operands) {
      this.operands = operands;
   }

   /**
    * Returns a string representation of the specified {@code Term}.
    * <p>
    * This method does take account of current operator declarations - thus an infix operator will be printed out
    * between its arguments. This method represents lists as a comma separated sequence of elements enclosed in square
    * brackets.
    * <p>
    * For example: <pre>
    * Term structure = Structure.createStructure("+", new IntegerNumber(1), new IntegerNumber(2));
    * Term list = ListFactory.create(new Term[]{new Atom("a"), Atom("b"), Atom("c")});
    * System.out.println("Structure.toString():      "+structure.toString());
    * System.out.println("Write.toString(structure): "+write.toString(structure));
    * System.out.println("List.toString():           "+list.toString());
    * System.out.println("Write.toString(list):      "+write.toString(list));
    * </pre> would print out: <pre>
    * Structure.toString():      +(1, 2)
    * Write.toString(structure): 1 + 2
    * List.toString():           .(a, .(b, .(c, [])))
    * Write.toString(list):      [a,b,c]
    * </pre>
    *
    * @param t the {@code Term} to represent as a string
    * @return a string representation of the specified {@code Term}
    */
   public String formatTerm(Term t) {
      StringBuilder sb = new StringBuilder();
      write(t, sb);
      return sb.toString();
   }

   private void write(Term t, StringBuilder sb) {
      switch (t.getType()) {
         case STRUCTURE:
            writePredicate(t, sb);
            break;
         case LIST:
            writeList(t, sb);
            break;
         case EMPTY_LIST:
            sb.append("[]");
            break;
         case VARIABLE:
            sb.append(((Variable) t).getId());
            break;
         default:
            sb.append(t.toString());
      }
   }

   private void writeList(Term p, StringBuilder sb) {
      sb.append('[');
      Term head = p.getArgument(0);
      Term tail = p.getArgument(1);
      write(head, sb);
      Term list;
      while ((list = getList(tail)) != null) {
         sb.append(',');
         write(list.getArgument(0), sb);
         tail = list.getArgument(1);
      }

      if (tail.getType() != TermType.EMPTY_LIST) {
         sb.append('|');
         write(tail, sb);
      }
      sb.append(']');
   }

   private static Term getList(Term t) {
      if (t.getType() == TermType.LIST) {
         return t;
      } else {
         return null;
      }
   }

   private void writePredicate(Term p, StringBuilder sb) {
      if (isInfixOperator(p)) {
         writeInfixOperator(p, sb);
      } else if (isPrefixOperator(p)) {
         writePrefixOperator(p, sb);
      } else if (isPostfixOperator(p)) {
         writePostfixOperator(p, sb);
      } else {
         writeNonOperatorPredicate(p, sb);
      }
   }

   private boolean isInfixOperator(Term t) {
      return t.getType() == TermType.STRUCTURE && t.getNumberOfArguments() == 2 && operands.infix(t.getName());
   }

   private void writeInfixOperator(Term p, StringBuilder sb) {
      Term first = p.getArgument(0);
      Term second = p.getArgument(1);
      int priority = operands.getInfixPriority(p.getName());

      if (shouldLeftArgumentBeBracketed(first, p, priority)) {
         sb.append('(');
         write(first, sb);
         sb.append(')');
      } else {
         write(first, sb);
      }

      sb.append(' ').append(p.getName()).append(' ');

      if (shouldRightArgumentBeBracketed(second, p, priority)) {
         sb.append('(');
         write(second, sb);
         sb.append(')');
      } else {
         write(second, sb);
      }
   }

   private boolean shouldLeftArgumentBeBracketed(Term next, Term parentInfixTerm, int parentInfixPriority) {
      if (!next.getType().isStructure()) {
         return false;
      }

      int nextPriority = getPriority(next);

      if (nextPriority == parentInfixPriority) {
         if (next.getNumberOfArguments() == 2) {
            if (operands.xfx(next.getName()) || operands.xfx(parentInfixTerm.getName())) {
               return true;
            }
            if (operands.yfx(next.getName()) && operands.xfy(parentInfixTerm.getName())) {
               return true;
            }
         } else if (next.getNumberOfArguments() == 1 && operands.prefix(next.getName())) {
            if (operands.xfx(parentInfixTerm.getName())) {
               return true;
            }
            if (operands.fx(next.getName()) && operands.xfy(parentInfixTerm.getName())) {
               return true;
            }
         }
         return false;
      } else {
         return nextPriority > parentInfixPriority;
      }
   }

   private boolean shouldRightArgumentBeBracketed(Term next, Term parentInfixTerm, int parentInfixPriority) {
      if (!next.getType().isStructure()) {
         return false;
      }

      int nextPriority = getPriority(next);

      if (parentInfixPriority == nextPriority) {
         if (next.getNumberOfArguments() == 2) {
            if (operands.xfx(next.getName()) || operands.xfx(parentInfixTerm.getName())) {
               return true;
            }
            if (operands.yfx(parentInfixTerm.getName())) {
               return true;
            }
         } else if (next.getNumberOfArguments() == 1 && operands.postfix(next.getName())) {
            if (operands.xfx(parentInfixTerm.getName())) {
               return true;
            }
            if (operands.xf(next.getName()) && operands.yfx(parentInfixTerm.getName())) {
               return true;
            }
         }
         return false;
      } else {
         return nextPriority > parentInfixPriority;
      }
   }

   private boolean isPrefixOperator(Term t) {
      return t.getType() == TermType.STRUCTURE && t.getNumberOfArguments() == 1 && operands.prefix(t.getName());
   }

   private void writePrefixOperator(Term p, StringBuilder sb) {
      sb.append(p.getName()).append(' ');

      int p1 = operands.getPrefixPriority(p.getName());
      int p2 = getPriority(p.getArgument(0));
      if (p1 < p2 || (p1 == p2 && (operands.fx(p.getName()) || !canBePreceededByEqualPriority(p.getArgument(0))))) {
         sb.append('(');
         write(p.getArgument(0), sb);
         sb.append(')');
      } else {
         write(p.getArgument(0), sb);
      }
   }

   private boolean isPostfixOperator(Term t) {
      return t.getType() == TermType.STRUCTURE && t.getNumberOfArguments() == 1 && operands.postfix(t.getName());
   }

   private void writePostfixOperator(Term p, StringBuilder sb) {
      int p1 = operands.getPostfixPriority(p.getName());
      int p2 = getPriority(p.getArgument(0));
      if (p1 < p2 || (p1 == p2 && (operands.xf(p.getName()) || !canBePreceededByEqualPriority(p.getArgument(0))))) {
         sb.append('(');
         write(p.getArgument(0), sb);
         sb.append(')');
      } else {
         write(p.getArgument(0), sb);
      }

      sb.append(' ').append(p.getName());
   }

   private boolean canBePreceededByEqualPriority(Term next) {
      if (next.getNumberOfArguments() == 2 && operands.infix(next.getName())) {
         return operands.xfy(next.getName());
      } else {
         return true;
      }
   }

   private int getPriority(Term next) {
      if (next.getNumberOfArguments() == 2 && operands.infix(next.getName())) {
         return operands.getInfixPriority(next.getName());
      } else if (next.getNumberOfArguments() == 1 && operands.prefix(next.getName())) {
         return operands.getPrefixPriority(next.getName());
      } else if (next.getNumberOfArguments() == 1 && operands.postfix(next.getName())) {
         return operands.getPostfixPriority(next.getName());
      } else {
         return -1;
      }
   }

   private void writeNonOperatorPredicate(Term p, StringBuilder sb) {
      String name = p.getName();
      sb.append(name);
      sb.append("(");
      for (int i = 0; i < p.getNumberOfArguments(); i++) {
         if (i != 0) {
            sb.append(", ");
         }
         write(p.getArgument(i), sb);
      }
      sb.append(")");
   }
}
