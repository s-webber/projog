/*
 * Copyright 2013-2014 S. Webber
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
package org.projog.core.parser;

import static org.projog.core.kb.KnowledgeBaseUtils.isQuestionOrDirectiveFunctionCall;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.Map;

import org.projog.core.ProjogException;
import org.projog.core.kb.KnowledgeBase;
import org.projog.core.predicate.Predicate;
import org.projog.core.predicate.PredicateKey;
import org.projog.core.predicate.udp.ClauseModel;
import org.projog.core.predicate.udp.StaticUserDefinedPredicateFactory;
import org.projog.core.predicate.udp.UserDefinedPredicateFactory;
import org.projog.core.term.Term;

/**
 * Populates a {@link KnowledgeBase} with clauses parsed from Prolog syntax.
 * <p>
 * <img src="doc-files/ProjogSourceReader.png">
 * </p>
 */
public final class ProjogSourceReader {
   private final KnowledgeBase kb;
   private final Map<PredicateKey, UserDefinedPredicateFactory> userDefinedPredicates = new LinkedHashMap<>();

   /**
    * Populates the KnowledgeBase with clauses defined in the file.
    *
    * @param kb the KnowledgeBase to add the clauses to
    * @param prologSourceFile source of the prolog syntax defining clauses to add to the KnowledgeBase
    * @throws ProjogException if there is any problem parsing the syntax or adding the new clauses to the KnowledgeBase
    */
   public static void parseFile(KnowledgeBase kb, File prologSourceFile) {
      notifyReadingFromFileSystem(kb, prologSourceFile);
      try (Reader reader = new FileReader(prologSourceFile)) {
         ProjogSourceReader projogSourceReader = new ProjogSourceReader(kb);
         projogSourceReader.parse(reader);
      } catch (Exception e) {
         throw new ProjogException("Could not read prolog source from file: " + prologSourceFile + " due to: " + e, e);
      }
   }

   /**
    * Populates the KnowledgeBase with clauses defined in the specified resource.
    * <p>
    * If {@code prologSourceResourceName} refers to an existing file on the file system then that file is used as the
    * source of the prolog syntax else {@code prologSourceResourceName} is read from the classpath.
    *
    * @param kb the KnowledgeBase to add the clauses to
    * @param prologSourceResourceName source of the prolog syntax defining clauses to add to the KnowledgeBase
    * @throws ProjogException if there is any problem parsing the syntax or adding the new clauses to the KnowledgeBase
    */
   public static void parseResource(KnowledgeBase kb, String prologSourceResourceName) {
      try (Reader reader = getReader(kb, prologSourceResourceName)) {
         ProjogSourceReader projogSourceReader = new ProjogSourceReader(kb);
         projogSourceReader.parse(reader);
      } catch (Exception e) {
         throw new ProjogException("Could not read prolog source from resource: " + prologSourceResourceName, e);
      }
   }

   /**
    * Populates the KnowledgeBase with clauses read from the Reader.
    * <p>
    * Note that this method will call {@code close()} on the specified reader - regardless of whether this method
    * completes successfully or if an exception is thrown.
    *
    * @param kb the KnowledgeBase to add the clauses to
    * @param reader source of the prolog syntax defining clauses to add to the KnowledgeBase
    * @throws ProjogException if there is any problem parsing the syntax or adding the new clauses to the KnowledgeBase
    */
   public static void parseReader(KnowledgeBase kb, Reader reader) {
      try {
         ProjogSourceReader projogSourceReader = new ProjogSourceReader(kb);
         projogSourceReader.parse(reader);
      } catch (Exception e) {
         throw new ProjogException("Could not read prolog source from java.io.Reader: " + reader, e);
      } finally {
         try {
            reader.close();
         } catch (Exception e) {
         }
      }
   }

   /**
    * Creates a new {@code Reader} for the specified resource.
    * <p>
    * If {@code resourceName} refers to an existing file on the filesystem then that file is used as the source of the
    * {@code Reader}. If there is no existing file on the filesystem matching {@code resourceName} then an attempt is
    * made to read the resource from the classpath.
    */
   private static Reader getReader(KnowledgeBase kb, String resourceName) throws IOException {
      File f = new File(resourceName);
      if (f.exists()) {
         notifyReadingFromFileSystem(kb, f);
         return new FileReader(resourceName);
      } else {
         notifyReadingFromClasspath(kb, resourceName);
         InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(resourceName);
         if (is == null) {
            throw new ProjogException("Cannot find resource: " + resourceName);
         }
         return new InputStreamReader(is);
      }
   }

   private static void notifyReadingFromFileSystem(KnowledgeBase kb, File file) {
      kb.getProjogListeners().notifyInfo("Reading prolog source in: " + file + " from file system");
   }

   private static void notifyReadingFromClasspath(KnowledgeBase kb, String resourceName) {
      kb.getProjogListeners().notifyInfo("Reading prolog source in: " + resourceName + " from classpath");
   }

   private ProjogSourceReader(KnowledgeBase kb) {
      this.kb = kb;
   }

   private void parse(Reader reader) {
      try {
         parseTerms(reader);
         addUserDefinedPredicatesToKnowledgeBase();
      } finally {
         try {
            reader.close();
         } catch (Exception e) {
         }
      }
   }

   private void parseTerms(Reader reader) {
      SentenceParser sp = SentenceParser.getInstance(reader, kb.getOperands());
      Term t;
      while ((t = sp.parseSentence()) != null) {
         if (isQuestionOrDirectiveFunctionCall(t)) {
            processQuestion(t);
         } else {
            storeParsedTerm(t);
         }
      }
   }

   /**
    * @param t structure with name of {@code ?-} and a single argument.
    */
   private void processQuestion(Term t) {
      Predicate e = kb.getPredicates().getPredicate(t.getArgument(0));
      while (e.evaluate() && e.couldReevaluationSucceed()) {
         // keep re-evaluating until fail
      }
   }

   private void storeParsedTerm(Term parsedTerm) {
      ClauseModel clauseModel = ClauseModel.createClauseModel(parsedTerm);
      Term parsedTermConsequent = clauseModel.getConsequent();
      UserDefinedPredicateFactory userDefinedPredicate = createOrReturnUserDefinedPredicate(parsedTermConsequent);
      userDefinedPredicate.addLast(clauseModel);
   }

   private UserDefinedPredicateFactory createOrReturnUserDefinedPredicate(Term t) {
      PredicateKey key = PredicateKey.createForTerm(t);
      UserDefinedPredicateFactory userDefinedPredicate = userDefinedPredicates.get(key);
      if (userDefinedPredicate == null) {
         userDefinedPredicate = new StaticUserDefinedPredicateFactory(kb, key);
         userDefinedPredicates.put(key, userDefinedPredicate);
      }
      return userDefinedPredicate;
   }

   private void addUserDefinedPredicatesToKnowledgeBase() {
      for (UserDefinedPredicateFactory userDefinedPredicate : userDefinedPredicates.values()) {
         kb.getPredicates().addUserDefinedPredicate(userDefinedPredicate);
      }
      for (UserDefinedPredicateFactory userDefinedPredicate : userDefinedPredicates.values()) {
         if (userDefinedPredicate instanceof StaticUserDefinedPredicateFactory) {
            ((StaticUserDefinedPredicateFactory) userDefinedPredicate).compile();
         }
      }
   }
}
