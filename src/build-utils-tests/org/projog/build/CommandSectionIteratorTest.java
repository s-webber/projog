package org.projog.build;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.projog.build.BuildUtilsTestConstants.TEST_RESOURCES_DIR;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class CommandSectionIteratorTest {
   @Test
   public void testSuccess() throws Exception {
      TableOfContentsEntryFactory ctr = new TableOfContentsEntryFactory();
      ctr.createSectionItem("", "");
      ctr.createSectionHeader("");

      List<CodeExampleWebPage> pages = createPages("org.projog.qwe.Xyz", "org.projog.abc.Qwerty", "org.projog.xyz.Abc", "org.projog.qwe.Fghjkl");
      Map<String, String> packageDescriptions = new HashMap<>();
      packageDescriptions.put("org.projog.abc", "w");
      packageDescriptions.put("org.projog.qwe", "x");
      packageDescriptions.put("org.projog.xyz", "y");
      CommandSectionIterator itr = new CommandSectionIterator(pages, packageDescriptions, ctr);

      assertPackageDescription(itr, "w");
      assertCommandLink(itr, "2.1.", "How to Qwerty", "Qwerty.html");
      assertPackageDescription(itr, "x");
      assertCommandLink(itr, "2.2.", "Fghhjl Tile", "Fghjkl.html");
      assertCommandLink(itr, "2.3.", "About Xyz", "Xyz.html");
      assertPackageDescription(itr, "y");
      assertCommandLink(itr, "2.4.", "Intro to Abc", "Abc.html");
      assertFalse(itr.hasNext());
   }

   @Test
   public void testFailure() throws Exception {
      List<CodeExampleWebPage> pages = createPages("org.projog.qwe.Xyz");
      CommandSectionIterator itr = new CommandSectionIterator(pages, new HashMap<String, String>(), new TableOfContentsEntryFactory());

      assertTrue(itr.hasNext());
      try {
         itr.next();
         fail();
      } catch (RuntimeException e) {
         assertEquals("Cannot find description for: org.projog.qwe", e.getMessage());
      }
   }

   private void assertPackageDescription(CommandSectionIterator itr, String description) {
      assertTrue(itr.hasNext());
      TableOfContentsEntry e = itr.next();
      assertTrue(e.isDescription());
      assertEquals(description, e.getTitle());
   }

   private void assertCommandLink(CommandSectionIterator itr, String index, String title, String fileName) {
      assertTrue(itr.hasNext());
      TableOfContentsEntry e = itr.next();
      assertEquals(index, e.getIndex());
      assertEquals(title, e.getTitle());
      assertEquals(fileName, e.getFileName());
   }

   private List<CodeExampleWebPage> createPages(String... classNames) throws FileNotFoundException {
      List<CodeExampleWebPage> indexOfGeneratedPages = new ArrayList<>();
      for (String className : classNames) {
         CodeExampleWebPage p = createCodeExampleWebPage(className);
         indexOfGeneratedPages.add(p);
      }
      return indexOfGeneratedPages;
   }

   private CodeExampleWebPage createCodeExampleWebPage(String className) {
      return CodeExampleWebPage.create(new File(TEST_RESOURCES_DIR, className + ".pl"));
   }
}
