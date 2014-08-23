package org.projog.build;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

/**
 * Constants and utility methods used in the build process.
 */
class BuildUtilsConstants {
   static final File BUILD_DIR = new File("build");
   static final File WEB_SRC_DIR = new File("web");
   static final File DOCS_OUTPUT_DIR = new File(BUILD_DIR, "docs");
   static final File SCRIPTS_OUTPUT_DIR = new File(BUILD_DIR, "scripts");
   static final File MANUAL_TEMPLATE = new File(WEB_SRC_DIR, "manual.txt");
   static final File STATIC_PAGES_LIST = new File(WEB_SRC_DIR, "static_pages.properties");
   static final String SOURCE_INPUT_DIR_NAME = "src/core/";
   static final File SOURCE_INPUT_DIR = new File(SOURCE_INPUT_DIR_NAME);
   static final String LINE_BREAK = "\r\n";
   static final String HTML_FILE_EXTENSION = ".html";
   static final String PROLOG_FILE_EXTENSION = ".pl";
   static final String TEXT_FILE_EXTENSION = ".txt";
   static final String MANUAL_HTML = "manual" + HTML_FILE_EXTENSION;
   static final String HEADER_HTML = "header" + HTML_FILE_EXTENSION;
   static final String FOOTER_HTML = "footer" + HTML_FILE_EXTENSION;

   /**
    * Returns the contents of the specified file as a byte array.
    * 
    * @param f file to read
    * @return contents of file
    */
   static byte[] toByteArray(File f) {
      try {
         return Files.readAllBytes(f.toPath());
      } catch (Exception e) {
         throw new RuntimeException("could not read file: " + f, e);
      }
   }

   /**
    * Returns list of lines contained in specified text file.
    * 
    * @param f text file to read
    * @return list of lines contained in specified file
    */
   static List<String> readFile(File f) {
      try {
         return Files.readAllLines(f.toPath(), Charset.defaultCharset());
      } catch (Exception e) {
         throw new RuntimeException("could not read text file: " + f, e);
      }
   }
}