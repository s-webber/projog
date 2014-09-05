package org.projog.build;

import static java.lang.Character.isLetter;
import static java.lang.Character.toUpperCase;
import static org.projog.TestUtils.ADD_CALCULATABLE_KEY;
import static org.projog.TestUtils.ADD_PREDICATE_KEY;
import static org.projog.TestUtils.BOOTSTRAP_FILE;
import static org.projog.TestUtils.parseTermsFromFile;
import static org.projog.build.BuildUtilsConstants.COMMANDS_INDEX_FILE;
import static org.projog.build.BuildUtilsConstants.htmlEncode;
import static org.projog.core.KnowledgeBaseUtils.QUESTION_PREDICATE_NAME;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import org.projog.core.PredicateKey;
import org.projog.core.term.Term;

/** Generates HTML page listing alphabetically ordered index of all built-in predicates. */
class BuiltInPredicatesIndexPage {
   /**
    * A {@code Comparator} for {@code Character} that handles {@code null} values.
    * <p>
    * {@code null} values are considered to be less than all other values.
    */
   private static final Comparator<Character> NULLABLE_CHARACTER_COMPARATOR = new Comparator<Character>() {
      @Override
      public int compare(Character o1, Character o2) {
         if (o1 == o2) {
            return 0;
         } else if (o1 == null) {
            return -1;
         } else if (o2 == null) {
            return 1;
         } else {
            return o1.compareTo(o2);
         }
      }
   };

   static void produceBuiltInPredicatesIndexPage() throws Exception {
      List<Term> terms = getCommands();
      List<BuiltIn> builtIns = new ArrayList<>();
      for (Term t : terms) {
         PredicateKey key = createKey(t);
         String predicateFactory = getPredicateFactoryName(t);
         BuiltIn builtIn = new BuiltIn(key, predicateFactory);
         builtIns.add(builtIn);
      }
      produce(builtIns);
   }

   private static PredicateKey createKey(Term t) {
      Term nameAndArity = t.getArgument(0);
      PredicateKey key = PredicateKey.createFromNameAndArity(nameAndArity);
      return key;
   }

   private static String getPredicateFactoryName(Term t) {
      return t.getArgument(1).getName();
   }

   private static List<Term> getCommands() {
      List<Term> result = new ArrayList<Term>();
      Term[] terms = parseTermsFromFile(BOOTSTRAP_FILE);
      for (Term next : terms) {
         if (QUESTION_PREDICATE_NAME.equals(next.getName())) {
            Term t = next.getArgument(0);
            PredicateKey key = PredicateKey.createForTerm(t);
            if (ADD_PREDICATE_KEY.equals(key) || ADD_CALCULATABLE_KEY.equals(key)) {
               result.add(t);
            }
         }
      }
      return result;
   }

   private static void produce(List<BuiltIn> args) throws Exception {
      Map<Character, TreeSet<BuiltIn>> index = groupByFirstCharacter(args);
      print(index);
   }

   private static Map<Character, TreeSet<BuiltIn>> groupByFirstCharacter(List<BuiltIn> args) {
      // Can't use default TreeMap constructor as we want to be able to use null as a key.
      // (As we use null to group <i>all</i> non-alphanumeric characters.)
      Map<Character, TreeSet<BuiltIn>> index = new TreeMap<>(NULLABLE_CHARACTER_COMPARATOR);
      for (BuiltIn s : args) {
         TreeSet<BuiltIn> byLetter = getSetForString(index, s);
         byLetter.add(s);
      }
      return index;
   }

   private static TreeSet<BuiltIn> getSetForString(Map<Character, TreeSet<BuiltIn>> index, BuiltIn s) {
      return getOrCreateSet(index, getFirstLetter(s.key.getName()));
   }

   private static Character getFirstLetter(String s) {
      char c = toUpperCase(s.charAt(0));
      if (isLetter(c)) {
         return c;
      } else {
         return null;
      }
   }

   private static TreeSet<BuiltIn> getOrCreateSet(Map<Character, TreeSet<BuiltIn>> index, Character c) {
      TreeSet<BuiltIn> byLetter = index.get(c);
      if (byLetter == null) {
         byLetter = new TreeSet<>();
         index.put(c, byLetter);
      }
      return byLetter;
   }

   private static void print(Map<Character, TreeSet<BuiltIn>> index) throws Exception {
      try (PrintWriter w = new PrintWriter(COMMANDS_INDEX_FILE)) {
         printIndex(w, index);
         printSections(w, index);
      }
   }

   private static void printIndex(PrintWriter w, Map<Character, TreeSet<BuiltIn>> index) {
      w.print("<center>");
      for (Character c : index.keySet()) {
         if (c != null) {
            w.print(" | <a href=\"#");
            w.print(c);
            w.print("\">");
            w.print(c);
            w.print("</a>");
         }
      }
      w.println(" |</center><hr>");
   }

   private static void printSections(PrintWriter w, Map<Character, TreeSet<BuiltIn>> index) {
      for (Entry<Character, TreeSet<BuiltIn>> e : index.entrySet()) {
         printSection(w, e.getKey(), e.getValue());
      }
   }

   private static void printSection(PrintWriter w, Character key, TreeSet<BuiltIn> commands) {
      if (key != null) {
         printHeader(w, key);
      }
      printCommands(w, commands);
   }

   private static void printHeader(PrintWriter w, Character key) {
      w.print("<h2><a name=\"");
      w.print(key);
      w.print("\">");
      w.print(key);
      w.print("</a></h2>");
      w.println();
   }

   private static void printCommands(PrintWriter w, TreeSet<BuiltIn> commands) {
      for (BuiltIn b : commands) {
         printCommand(w, b);
      }
   }

   private static void printCommand(PrintWriter w, BuiltIn b) {
      w.print("| <a href=\"");
      w.print(getTarget(b.predicateFactory));
      w.print("\">");
      w.print(htmlEncode(b.key.toString()));
      w.println("</a>");
   }

   /**
    * Returns the target of a link.
    * <p>
    * Target will be the class name (minus the package) plus a ".html" extension.
    * 
    * @param input e.g.: {@code org.projog.Xyz} or {@code org.projog.Xyz/getInstance}
    * @return e.g.: {@code Xyz.html}
    */
   private static String getTarget(String input) {
      int dotPos = input.lastIndexOf('.');
      int slashPos = input.indexOf('/', dotPos);
      int endPos = slashPos == -1 ? input.length() : slashPos;
      return input.substring(dotPos + 1, endPos) + ".html";
   }

   private static class BuiltIn implements Comparable<BuiltIn> {
      final PredicateKey key;
      final String predicateFactory;

      BuiltIn(PredicateKey key, String predicateFactory) {
         this.key = key;
         this.predicateFactory = predicateFactory;
      }

      @Override
      public int compareTo(BuiltIn o) {
         return key.compareTo(o.key);
      }
   }
}
