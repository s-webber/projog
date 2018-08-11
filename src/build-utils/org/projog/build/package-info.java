/**
 * Provides utilities executed by the Projog build.xml Ant script to generate system test files, run system tests and generate web content.
 * <p>
 * <ul>
 * <li><code>ant sys-test.generate</code> launches {@link org.projog.test.ProjogTestGenerator} which extracts
 * Prolog syntax contained in the comments of java source files to produce system tests files.</li>
 * <li><code>ant sys-test.run</code> launches {@link org.projog.test.ProjogTestRunner} which runs the
 * queries contained in the system test files and compares the actual results to the expected answers.</li>
 * <li><code>ant web</code> launches {@link org.projog.build.HtmlGenerator} which produces the web
 * documentation using a combination of the files in <code>web</code> and the system test files.</li>
 * </ul>
 * {@link org.projog.test.ProjogTestParser} is used to parse the system test files and represent the content as
 * a collection of {@link org.projog.test.ProjogTestContent} instances.
 * </p>
 */
package org.projog.build;
