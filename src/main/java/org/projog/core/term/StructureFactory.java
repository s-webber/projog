/*
 * Copyright 2025 S. Webber
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

import java.util.Arrays;
import java.util.Map;

/**
 * Creates implementations of {@link Term} consisting of a functor (name) and a number of other {@link Term} arguments.
 * <p>
 * Also known as a "compound term".
 */
public final class StructureFactory {
   public static Term createStructure(String functor, Term[] args) {
      switch (args.length) {
         case 0:
            return new Atom(functor);
         case 1:
            return createStructure(functor, args[0]);
         case 2:
            return createStructure(functor, args[0], args[1]);
         case 3:
            return new ThreeArgumentStructure(functor, args[0], args[1], args[2]);
         case 4:
            return new FourArgumentStructure(functor, args[0], args[1], args[2], args[3]);
         default:
            return new MultipleArgumentStructure(functor, args, isImmutable(args));
      }
   }

   public static Term createStructure(String functor, Term argument) {
      return new SingleArgumentStructure(functor, argument);
   }

   public static Term createStructure(String functor, Term firstArgument, Term secondArgument) {
      if (ListFactory.LIST_PREDICATE_NAME.equals(functor)) {
         return new List(firstArgument, secondArgument);
      } else {
         return new TwoArgumentStructure(functor, firstArgument, secondArgument);
      }
   }

   private static boolean isImmutable(Term[] args) {
      for (Term t : args) {
         if (t.isImmutable() == false) {
            return false;
         }
      }
      return true;
   }

   private static final class SingleArgumentStructure implements Term {
      private final String functor;
      private final Term argument;

      private SingleArgumentStructure(String functor, Term argument) {
         this.functor = functor;
         this.argument = argument;
      }

      @Override
      public String getName() {
         return functor;
      }

      @Override
      public Term firstArgument() {
         return argument;
      }

      @Override
      public int getNumberOfArguments() {
         return 1;
      }

      @Override
      public Term getArgument(int index) {
         if (index == 0) {
            return argument;
         } else {
            throw new IllegalArgumentException();
         }
      }

      @Override
      public TermType getType() {
         return TermType.STRUCTURE;
      }

      @Override
      public SingleArgumentStructure copy(Map<Variable, Term> sharedVariables) {
         if (argument.isImmutable()) {
            return this;
         }

         Term newArgument = argument.copy(sharedVariables);
         if (newArgument == argument) {
            return this;
         } else {
            return new SingleArgumentStructure(functor, newArgument);
         }
      }

      @Override
      public SingleArgumentStructure getTerm() {
         if (argument.isImmutable()) {
            return this;
         }

         Term newArgument = argument.getTerm();
         if (newArgument == argument) {
            return this;
         } else {
            return new SingleArgumentStructure(functor, newArgument);
         }
      }

      @Override
      public boolean unify(Term t) {
         TermType tType = t.getType();
         if (tType == TermType.STRUCTURE && t.getNumberOfArguments() == 1) {
            return functor.equals(t.getName()) && argument.unify(t.firstArgument());
         } else if (tType.isVariable()) {
            return t.unify(this);
         } else {
            return false;
         }
      }

      @Override
      public void backtrack() {
         argument.backtrack();
      }

      @Override
      public boolean isImmutable() {
         return argument.isImmutable();
      }

      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         }

         if (o.getClass() == SingleArgumentStructure.class) {
            SingleArgumentStructure other = (SingleArgumentStructure) o;
            return functor.equals(other.functor) && argument.equals(other.argument);
         }

         return false;
      }

      @Override
      public int hashCode() {
         return functor.hashCode() + argument.hashCode();
      }

      @Override
      public String toString() {
         return functor + "(" + argument + ")";
      }
   }

   private static final class TwoArgumentStructure implements Term {
      private final String functor;
      private final Term first;
      private final Term second;
      private boolean isImmutable;

      private TwoArgumentStructure(String functor, Term first, Term second) {
         this.functor = functor;
         this.first = first;
         this.second = second;
         this.isImmutable = first.isImmutable() && second.isImmutable();
      }

      @Override
      public String getName() {
         return functor;
      }

      @Override
      public Term firstArgument() {
         return first;
      }

      @Override
      public Term secondArgument() {
         return second;
      }

      @Override
      public int getNumberOfArguments() {
         return 2;
      }

      @Override
      public Term getArgument(int index) {
         switch (index) {
            case 0:
               return first;
            case 1:
               return second;
            default:
               throw new IllegalArgumentException();
         }
      }

      @Override
      public TermType getType() {
         return TermType.STRUCTURE;
      }

      @Override
      public TwoArgumentStructure copy(Map<Variable, Term> sharedVariables) {
         if (isImmutable) {
            return this;
         }

         Term newFirst = first.copy(sharedVariables);
         Term newSecond = second.copy(sharedVariables);
         if (newFirst == first && newSecond == second) {
            return this;
         } else {
            return new TwoArgumentStructure(functor, newFirst, newSecond);
         }
      }

      @Override
      public TwoArgumentStructure getTerm() {
         if (isImmutable) {
            return this;
         }

         Term newFirst = first.getTerm();
         Term newSecond = second.getTerm();
         if (newFirst == first && newSecond == second) {
            return this;
         } else {
            return new TwoArgumentStructure(functor, newFirst, newSecond);
         }
      }

      @Override
      public boolean unify(Term t) {
         TermType tType = t.getType();
         if (tType == TermType.STRUCTURE && t.getNumberOfArguments() == 2) {
            return functor.equals(t.getName()) && first.unify(t.firstArgument()) && second.unify(t.secondArgument());
         } else if (tType.isVariable()) {
            return t.unify(this);
         } else {
            return false;
         }
      }

      @Override
      public void backtrack() {
         first.backtrack();
         second.backtrack();
      }

      @Override
      public boolean isImmutable() {
         return isImmutable;
      }

      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         }

         if (o.getClass() == TwoArgumentStructure.class) {
            TwoArgumentStructure other = (TwoArgumentStructure) o;
            return functor.equals(other.functor) && first.equals(other.first) && second.equals(other.second);
         }

         return false;
      }

      @Override
      public int hashCode() {
         return functor.hashCode() + first.hashCode() + (31 * second.hashCode());
      }

      @Override
      public String toString() {
         return functor + "(" + first + ", " + second + ")";
      }
   }

   private static final class ThreeArgumentStructure implements Term {
      private final String functor;
      private final Term first;
      private final Term second;
      private final Term third;
      private boolean isImmutable;

      private ThreeArgumentStructure(String functor, Term first, Term second, Term third) {
         this.functor = functor;
         this.first = first;
         this.second = second;
         this.third = third;
         this.isImmutable = first.isImmutable() && second.isImmutable() && third.isImmutable();
      }

      @Override
      public String getName() {
         return functor;
      }

      @Override
      public Term firstArgument() {
         return first;
      }

      @Override
      public Term secondArgument() {
         return second;
      }

      @Override
      public Term thirdArgument() {
         return third;
      }

      @Override
      public int getNumberOfArguments() {
         return 3;
      }

      @Override
      public Term getArgument(int index) {
         switch (index) {
            case 0:
               return first;
            case 1:
               return second;
            case 2:
               return third;
            default:
               throw new IllegalArgumentException();
         }
      }

      @Override
      public TermType getType() {
         return TermType.STRUCTURE;
      }

      @Override
      public ThreeArgumentStructure copy(Map<Variable, Term> sharedVariables) {
         if (isImmutable) {
            return this;
         }

         Term newFirst = first.copy(sharedVariables);
         Term newSecond = second.copy(sharedVariables);
         Term newThird = third.copy(sharedVariables);
         if (newFirst == first && newSecond == second && newThird == third) {
            return this;
         } else {
            return new ThreeArgumentStructure(functor, newFirst, newSecond, newThird);
         }
      }

      @Override
      public ThreeArgumentStructure getTerm() {
         if (isImmutable) {
            return this;
         }

         Term newFirst = first.getTerm();
         Term newSecond = second.getTerm();
         Term newThird = third.getTerm();
         if (newFirst == first && newSecond == second && newThird == third) {
            return this;
         } else {
            return new ThreeArgumentStructure(functor, newFirst, newSecond, newThird);
         }
      }

      @Override
      public boolean unify(Term t) {
         TermType tType = t.getType();
         if (tType == TermType.STRUCTURE && t.getNumberOfArguments() == 3) {
            return functor.equals(t.getName()) && first.unify(t.firstArgument()) && second.unify(t.secondArgument()) && third.unify(t.thirdArgument());
         } else if (tType.isVariable()) {
            return t.unify(this);
         } else {
            return false;
         }
      }

      @Override
      public void backtrack() {
         first.backtrack();
         second.backtrack();
         third.backtrack();
      }

      @Override
      public boolean isImmutable() {
         return isImmutable;
      }

      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         }

         if (o.getClass() == ThreeArgumentStructure.class) {
            ThreeArgumentStructure other = (ThreeArgumentStructure) o;
            return functor.equals(other.functor) && first.equals(other.first) && second.equals(other.second) && third.equals(other.third);
         }

         return false;
      }

      @Override
      public int hashCode() {
         return functor.hashCode() + first.hashCode() + (31 * second.hashCode()) + (17 * third.hashCode());
      }

      @Override
      public String toString() {
         return functor + "(" + first + ", " + second + ", " + third + ")";
      }
   }

   private static final class FourArgumentStructure implements Term {
      private final String functor;
      private final Term first;
      private final Term second;
      private final Term third;
      private final Term fourth;
      private boolean isImmutable;

      private FourArgumentStructure(String functor, Term first, Term second, Term third, Term fourth) {
         this.functor = functor;
         this.first = first;
         this.second = second;
         this.third = third;
         this.fourth = fourth;
         this.isImmutable = first.isImmutable() && second.isImmutable() && third.isImmutable() && fourth.isImmutable();
      }

      @Override
      public String getName() {
         return functor;
      }

      @Override
      public Term firstArgument() {
         return first;
      }

      @Override
      public Term secondArgument() {
         return second;
      }

      @Override
      public Term thirdArgument() {
         return third;
      }

      @Override
      public Term fourthArgument() {
         return fourth;
      }

      @Override
      public int getNumberOfArguments() {
         return 4;
      }

      @Override
      public Term getArgument(int index) {
         switch (index) {
            case 0:
               return first;
            case 1:
               return second;
            case 2:
               return third;
            case 3:
               return fourth;
            default:
               throw new IllegalArgumentException();
         }
      }

      @Override
      public TermType getType() {
         return TermType.STRUCTURE;
      }

      @Override
      public FourArgumentStructure copy(Map<Variable, Term> sharedVariables) {
         if (isImmutable) {
            return this;
         }

         Term newFirst = first.copy(sharedVariables);
         Term newSecond = second.copy(sharedVariables);
         Term newThird = third.copy(sharedVariables);
         Term newFourth = fourth.copy(sharedVariables);
         if (newFirst == first && newSecond == second && newThird == third && newFourth == fourth) {
            return this;
         } else {
            return new FourArgumentStructure(functor, newFirst, newSecond, newThird, newFourth);
         }
      }

      @Override
      public FourArgumentStructure getTerm() {
         if (isImmutable) {
            return this;
         }

         Term newFirst = first.getTerm();
         Term newSecond = second.getTerm();
         Term newThird = third.getTerm();
         Term newFourth = fourth.getTerm();
         if (newFirst == first && newSecond == second && newThird == third && newFourth == fourth) {
            return this;
         } else {
            return new FourArgumentStructure(functor, newFirst, newSecond, newThird, newFourth);
         }
      }

      @Override
      public boolean unify(Term t) {
         TermType tType = t.getType();
         if (tType == TermType.STRUCTURE && t.getNumberOfArguments() == 4) {
            return functor.equals(
                        t.getName())
                   && first.unify(t.firstArgument())
                   && second.unify(t.secondArgument())
                   && third.unify(t.thirdArgument())
                   && fourth.unify(t.fourthArgument());
         } else if (tType.isVariable()) {
            return t.unify(this);
         } else {
            return false;
         }
      }

      @Override
      public void backtrack() {
         first.backtrack();
         second.backtrack();
         third.backtrack();
         fourth.backtrack();
      }

      @Override
      public boolean isImmutable() {
         return isImmutable;
      }

      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         }

         if (o.getClass() == FourArgumentStructure.class) {
            FourArgumentStructure other = (FourArgumentStructure) o;
            return functor.equals(other.functor) && first.equals(other.first) && second.equals(other.second) && third.equals(other.third) && fourth.equals(other.fourth);
         }

         return false;
      }

      @Override
      public int hashCode() {
         return functor.hashCode() + first.hashCode() + (31 * second.hashCode()) + (17 * third.hashCode()) + (3 * fourth.hashCode());
      }

      @Override
      public String toString() {
         return functor + "(" + first + ", " + second + ", " + third + ", " + fourth + ")";
      }
   }

   private static final class MultipleArgumentStructure implements Term {
      private final String functor;
      private final Term[] args;
      private final boolean immutable;
      private final int hashCode;

      private MultipleArgumentStructure(String functor, Term[] args, boolean immutable) {
         this.functor = functor;
         this.args = args;
         this.immutable = immutable;
         this.hashCode = functor.hashCode() + Arrays.hashCode(args);
      }

      @Override
      public String getName() {
         return functor;
      }

      @Override
      public int getNumberOfArguments() {
         return args.length;
      }

      @Override
      public Term getArgument(int index) {
         return args[index];
      }

      @Override
      public TermType getType() {
         return TermType.STRUCTURE;
      }

      @Override
      public boolean isImmutable() {
         return immutable;
      }

      @Override
      public MultipleArgumentStructure getTerm() {
         if (immutable) {
            return this;
         } else {
            boolean returnThis = true;
            boolean newImmutable = true;
            Term newArgs[] = new Term[args.length];
            for (int i = 0; i < args.length; i++) {
               newArgs[i] = args[i].getTerm();
               if (newArgs[i] != args[i]) {
                  returnThis = false;
               }
               if (newArgs[i].isImmutable() == false) {
                  newImmutable = false;
               }
            }
            if (returnThis) {
               return this;
            } else {
               return new MultipleArgumentStructure(functor, newArgs, newImmutable);
            }
         }
      }

      @Override
      public MultipleArgumentStructure copy(Map<Variable, Term> sharedVariables) {
         if (immutable) {
            return this;
         } else {
            boolean returnThis = true;
            boolean newIsImmutable = true;
            Term newArgs[] = new Term[args.length];
            for (int i = 0; i < args.length; i++) {
               newArgs[i] = args[i].copy(sharedVariables);
               if (newArgs[i] != args[i]) {
                  returnThis = false;
               }
               if (newArgs[i].isImmutable() == false) {
                  newIsImmutable = false;
               }
            }
            if (returnThis) {
               return this;
            } else {
               return new MultipleArgumentStructure(functor, newArgs, newIsImmutable);
            }
         }
      }

      @Override
      public boolean unify(Term t) {
         TermType tType = t.getType();
         if (tType == TermType.STRUCTURE) {
            if (args.length != t.getNumberOfArguments()) {
               return false;
            }
            if (!functor.equals(t.getName())) {
               return false;
            }
            for (int i = 0; i < args.length; i++) {
               if (!args[i].unify(t.getArgument(i))) {
                  return false;
               }
            }
            return true;
         } else if (tType.isVariable()) {
            return t.unify(this);
         } else {
            return false;
         }
      }

      @Override
      public void backtrack() {
         if (!immutable) {
            TermUtils.backtrack(args);
         }
      }

      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         }

         if (o.getClass() == MultipleArgumentStructure.class && hashCode == o.hashCode()) {
            MultipleArgumentStructure other = (MultipleArgumentStructure) o;
            return functor.equals(other.functor) && Arrays.equals(args, other.args);
         }

         return false;
      }

      @Override
      public int hashCode() {
         return hashCode;
      }

      @Override
      public String toString() {
         StringBuilder sb = new StringBuilder();
         sb.append(functor);
         sb.append("(");
         boolean first = true;
         if (args != null) {
            for (Term arg : args) {
               if (first) {
                  first = false;
               } else {
                  sb.append(", ");
               }
               sb.append(arg);
            }
         }
         sb.append(")");
         return sb.toString();
      }
   }
}
