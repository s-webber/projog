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
package org.projog.core.predicate.builtin.classify;

import static java.lang.Character.MAX_VALUE;
import static org.projog.core.term.TermUtils.getAtomName;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.projog.core.predicate.AbstractPredicateFactory;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.predicate.builtin.io.GetChar;
import org.projog.core.term.Atom;
import org.projog.core.term.Term;
import org.projog.core.term.TermUtils;

/* TEST
%FAIL char_type(a, digit)
%TRUE char_type(a, lower)
%FAIL char_type(a, upper)
%TRUE char_type(a, alpha)
%TRUE char_type(a, alnum)
%FAIL char_type(a, white)

%FAIL char_type('A', digit)
%FAIL char_type('A', lower)
%TRUE char_type('A', upper)
%TRUE char_type('A', alpha)
%TRUE char_type('A', alnum)
%FAIL char_type('A', white)

%TRUE char_type('1', digit)
%FAIL char_type('1', lower)
%FAIL char_type('1', upper)
%FAIL char_type('1', alpha)
%TRUE char_type('1', alnum)
%FAIL char_type('1', white)

%FAIL char_type(' ', digit)
%FAIL char_type(' ', lower)
%FAIL char_type(' ', upper)
%FAIL char_type(' ', alpha)
%FAIL char_type(' ', alnum)
%TRUE char_type(' ', white)

%FAIL char_type('\\t ', digit)
%FAIL char_type('\\t', lower)
%FAIL char_type('\\t', upper)
%FAIL char_type('\\t', alpha)
%FAIL char_type('\\t', alnum)
%TRUE char_type('\\t', white)

%?- char_type(z, X)
% X=alnum
% X=alpha
% X=lower
%NO

%?- char_type(X, digit)
% X=0
% X=1
% X=2
% X=3
% X=4
% X=5
% X=6
% X=7
% X=8
% X=9
%NO

%?- char_type(X, upper)
% X=A
% X=B
% X=C
% X=D
% X=E
% X=F
% X=G
% X=H
% X=I
% X=J
% X=K
% X=L
% X=M
% X=N
% X=O
% X=P
% X=Q
% X=R
% X=S
% X=T
% X=U
% X=V
% X=W
% X=X
% X=Y
% X=Z
%NO

%?- char_type(X, lower)
% X=a
% X=b
% X=c
% X=d
% X=e
% X=f
% X=g
% X=h
% X=i
% X=j
% X=k
% X=l
% X=m
% X=n
% X=o
% X=p
% X=q
% X=r
% X=s
% X=t
% X=u
% X=v
% X=w
% X=x
% X=y
% X=z
%NO

%?- char_type(X, alnum)
% X=0
% X=1
% X=2
% X=3
% X=4
% X=5
% X=6
% X=7
% X=8
% X=9
% X=A
% X=B
% X=C
% X=D
% X=E
% X=F
% X=G
% X=H
% X=I
% X=J
% X=K
% X=L
% X=M
% X=N
% X=O
% X=P
% X=Q
% X=R
% X=S
% X=T
% X=U
% X=V
% X=W
% X=X
% X=Y
% X=Z
% X=a
% X=b
% X=c
% X=d
% X=e
% X=f
% X=g
% X=h
% X=i
% X=j
% X=k
% X=l
% X=m
% X=n
% X=o
% X=p
% X=q
% X=r
% X=s
% X=t
% X=u
% X=v
% X=w
% X=x
% X=y
% X=z
%NO

white_test :- char_type(X, white), write('>'), write(X), write('<'), nl, fail.
%?- white_test
%OUTPUT
%>\t<
%> <
%
%OUTPUT
%NO
*/
/**
 * <code>char_type(X,Y)</code> - classifies characters.
 * <p>
 * Succeeds if the character represented by <code>X</code> is a member of the character type represented by
 * <code>Y</code>. Supported character types are:
 * </p>
 * <ul>
 * <li><code>digit</code></li>
 * <li><code>upper</code> - upper case letter</li>
 * <li><code>lower</code> - lower case letter</li>
 * <li><code>alpha</code> - letter (upper or lower)</li>
 * <li><code>alnum</code> - letter (upper or lower) or digit</li>
 * <li><code>white</code> - whitespace</li>
 * </ul>
 */
public final class CharType extends AbstractPredicateFactory {
   private static final Type[] EMPTY_TYPES_ARRAY = new Type[] {};
   private static final Atom[] ALL_CHARACTERS = new Atom[MAX_VALUE + 2];
   static {
      for (int i = -1; i <= MAX_VALUE; i++) {
         ALL_CHARACTERS[i + 1] = new Atom(charToString(i));
      }
   }
   private static final Map<PredicateKey, Type> CHARACTER_TYPES_MAP = new LinkedHashMap<>();
   private static final Type[] CHARACTER_TYPES_ARRAY;
   static {
      // populate CHARACTER_TYPES_MAP

      Set<String> digits = createSetFromRange('0', '9');
      Set<String> upper = createSetFromRange('A', 'Z');
      Set<String> lower = createSetFromRange('a', 'z');

      addType("alnum", digits, upper, lower);
      addType("alpha", upper, lower);
      addType("digit", digits);
      addType("upper", upper);
      addType("lower", lower);
      addType("white", intsToStrings('\t', ' '));

      CHARACTER_TYPES_ARRAY = CHARACTER_TYPES_MAP.values().toArray(new Type[CHARACTER_TYPES_MAP.size()]);
   }

   /** @see GetChar#toString(int) */
   private static String charToString(int c) {
      if (c == '\t') {
         return "\\t";
      } else {
         return Character.toString((char) c);
      }
   }

   @SafeVarargs
   private static void addType(String id, Set<String>... charIdxs) {
      Set<String> superSet = new HashSet<>();
      for (Set<String> s : charIdxs) {
         superSet.addAll(s);
      }
      addType(id, superSet);
   }

   private static void addType(String id, Set<String> charIdxs) {
      Atom a = new Atom(id);
      PredicateKey key = PredicateKey.createForTerm(a);
      Type type = new Type(a, charIdxs);
      CHARACTER_TYPES_MAP.put(key, type);
   }

   private static Set<String> createSetFromRange(int from, int to) {
      int[] range = createRange(from, to);
      return intsToStrings(range);
   }

   private static int[] createRange(int from, int to) {
      int length = to - from + 1; // +1 to be inclusive
      int[] result = new int[length];
      for (int i = 0; i < length; i++) {
         result[i] = from + i;
      }
      return result;
   }

   private static Set<String> intsToStrings(int... ints) {
      Set<String> strings = new HashSet<>();
      for (int i : ints) {
         // +1 as "end of file" (-1) is stored at idx 0
         strings.add(ALL_CHARACTERS[i + 1].getName());
      }
      return strings;
   }

   @Override
   protected Predicate getPredicate(Term character, Term type) {
      Term[] characters;
      if (character.getType().isVariable()) {
         characters = ALL_CHARACTERS;
      } else {
         characters = new Term[] {character};
      }
      Type[] characterTypes = {};
      if (type.getType().isVariable()) {
         characterTypes = CHARACTER_TYPES_ARRAY;
      } else {
         PredicateKey key = PredicateKey.createForTerm(type);
         Type t = CHARACTER_TYPES_MAP.get(key);
         if (t != null) {
            characterTypes = new Type[] {t};
         } else {
            characters = TermUtils.EMPTY_ARRAY;
            characterTypes = EMPTY_TYPES_ARRAY;
         }
      }
      return new CharTypePredicate(character, type, new State(characters, characterTypes));
   }

   private final class CharTypePredicate implements Predicate {
      private final Term character;
      private final Term type;
      private final State state;

      private CharTypePredicate(Term character, Term type, State state) {
         this.character = character;
         this.type = type;
         this.state = state;
      }

      @Override
      public boolean evaluate() {
         while (state.hasNext()) {
            state.next();
            character.backtrack();
            type.backtrack();
            if (character.unify(state.getCharacter()) && state.getType().unify(character, type)) {
               return true;
            }
         }
         return false;
      }

      @Override
      public boolean couldReevaluationSucceed() {
         return state.hasNext();
      }
   }

   private static class State {
      final Term[] characters;
      final Type[] characterTypes;
      int characterCtr = 0;
      int characterTypeCtr = -1;

      State(Term[] characters, Type[] characterTypes) {
         this.characters = characters;
         this.characterTypes = characterTypes;
      }

      boolean hasNext() {
         return characterCtr + 1 < characters.length || characterTypeCtr + 1 < characterTypes.length;
      }

      void next() {
         characterTypeCtr++;
         if (characterTypeCtr == characterTypes.length) {
            characterTypeCtr = 0;
            characterCtr++;
         }
      }

      Term getCharacter() {
         return characters[characterCtr];
      }

      Type getType() {
         return characterTypes[characterTypeCtr];
      }
   }

   private static class Type {
      final Atom termId;
      final Set<String> characters;

      Type(Atom termId, Set<String> characters) {
         this.termId = termId;
         this.characters = characters;
      }

      boolean unify(Term character, Term type) {
         return characters.contains(getAtomName(character)) && type.unify(termId);
      }
   }
}
