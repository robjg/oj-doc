package org.oddjob.doc.html;

import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.beandocs.BeanDoc;
import org.oddjob.arooa.beandocs.ExampleDoc;
import org.oddjob.arooa.beandocs.PropertyDoc;
import org.oddjob.arooa.beandocs.element.BeanDocElement;
import org.oddjob.doc.doclet.IndexLine;
import org.oddjob.doc.util.DocUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Writes a single HTML Reference Page.
 */
public class HtmlPageWriter {

    private final String title;

    private final Path rootDirectory;

    private final HtmlContextProvider contextProvider;

    public HtmlPageWriter(String title, Path rootDirectory, HtmlContextProvider contextProvider) {
        this.title = title;
        this.rootDirectory = rootDirectory;
        this.contextProvider = contextProvider;
    }

    /**
     * Get the file name the page data should be created in.
     *
     * @return The file name.
     */
    public static String getFileName(String className) {
        return className.replace('.', '/') + ".html";
    }

    /**
     * Get the relative root directory.
     *
     * @return The directory path to the root.
     */
    public static String getIndexFile(String className) {

        StringBuilder path = new StringBuilder();
        int start = 0;
        while ((start = className.indexOf('.', start) + 1) > 0) {
            path.append("../");
        }
        return path + "index.html";
    }

    public String getTitle() {
        return title;
    }

    public Path getRootDirectory() {
        return rootDirectory;
    }

    public HtmlContextProvider getContextProvider() {
        return contextProvider;
    }

    /**
     * Write a single reference page.
     *
     * @param beanDoc
     */
    public IndexLine writePage(BeanDoc beanDoc) {

        String fileName = getFileName(beanDoc.getClassName());

        String pathToRoot = DocUtil.pathToRoot(beanDoc.getClassName());

        HtmlContext htmlContext = contextProvider.contextFor(pathToRoot);

        PageWriter pageWriter = new PageWriter(htmlContext);

        try {
            Path file = rootDirectory.resolve(fileName);
            Files.createDirectories(file.getParent());
            PrintWriter out = new PrintWriter(
                    new FileOutputStream(file.toFile()));

            pageWriter.writePageTo(beanDoc, out);

            return new IndexLine(beanDoc.getName(), fileName, beanDoc.getFirstSentence());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    class PageWriter {

        private final HtmlContext htmlContext;

        PageWriter(HtmlContext htmlContext) {
            this.htmlContext = htmlContext;
        }

        protected String toHtml(List<BeanDocElement> elements, String whenMissing) {
            if (elements == null || elements.isEmpty()) {
                return whenMissing;
            } else {
                return HtmlVisitor.visitAll(elements, htmlContext);
            }
        }

        public void writePageTo(BeanDoc beanDoc, PrintWriter out) {

            out.println("<html>");
            out.println("  <head>");
            out.println("    <title>" + title + " - " +
                    beanDoc.getName() + "</title>");
            out.println("  </head>");
            out.println("  <body>");
            out.println("  [<a href=\"" + getIndexFile(beanDoc.getClassName()) +
                    "\">Index</a>]");
            out.println("    <h1>" + beanDoc.getName() + "</h1>");
            out.println("    <hr/>");
            out.println(toHtml(beanDoc.getAllText(), "No Description."));

            PropertyDoc[] propertyDocs = beanDoc.getPropertyDocs();

            if (propertyDocs.length > 0) {
                out.println("    <hr/>");
                out.println("    <h3>Property Summary</h3>");
                out.println("    <table width='100%' border='1'" +
                        " cellpadding='3' cellspacing='0'>");
                int i = 0;
                for (PropertyDoc elem : propertyDocs) {
                    if (ConfiguredHow.HIDDEN == elem.getConfiguredHow()) {
                        continue;
                    }
                    out.println("    <tr>");
                    out.println("      <td><a href='#property" + ++i + "'>"
                            + elem.getPropertyName() + "</a></td>");
                    out.println("      <td>" +
                            toHtml(elem.getFirstSentence(), "&nbsp;") +
                            "</td>");
                    out.println("    </tr>");
                }
                out.println("    </table>");
            }

            ExampleDoc[] exampleDocs = beanDoc.getExampleDocs();

            if (exampleDocs.length > 0) {
                out.println("    <hr/>");
                out.println("    <h3>Example Summary</h3>");
                out.println("    <table width='100%' border='1'" +
                        " cellpadding='3' cellspacing='0'>");
                int i = 0;
                for (ExampleDoc elem : exampleDocs) {
                    out.println("    <tr>");
                    out.println("      <td><a href='#example" + ++i +
                            "'>Example " + i + "</a></td>");
                    out.println("      <td>" +
                            toHtml(elem.getFirstSentence(), "&nbsp;") +
                            "</td>");
                    out.println("    </tr>");
                }
                out.println("    </table>");
            }

            if (propertyDocs.length > 0) {
                out.println("    <hr/>");
                out.println("    <h3>Property Detail</h3>");
                int i = 0;
                for (PropertyDoc elem : propertyDocs) {
                    if (ConfiguredHow.HIDDEN == elem.getConfiguredHow()) {
                        continue;
                    }
                    out.println("    <a name='property" + ++i + "'><h4>" +
                            elem.getPropertyName() + "</h4></a>");
                    out.println("      <table style='font-size:smaller'>");
                    if (elem.getAccess() != PropertyDoc.Access.READ_ONLY) {
                        out.println("      <tr><td><i>Configured By</i></td><td>" +
                                elem.getConfiguredHow() + "</td></tr>");
                    }
                    out.println("      <tr><td><i>Access</i></td><td>" +
                            elem.getAccess() + "</td></tr>");
                    String required = elem.getRequired();
                    if (required != null) {
                        out.println("      <tr><td><i>Required</i></td><td>" +
                                required + "</td></tr>");
                    }
                    out.println("      </table>");
                    out.println("      <p>");
                    out.println(toHtml(elem.getAllText(), ""));
                    out.println("      </p>");
                }
            }

            if (exampleDocs.length > 0) {
                out.println("    <hr/>");
                out.println("    <h3>Examples</h3>");
                int i = 0;
                for (ExampleDoc example : exampleDocs) {
                    out.println("    <a name='example" + ++i +
                            "'><h4>Example " + i + "</h4></a>");
                    out.println("    <p>");
                    out.println(toHtml(example.getAllText(), ""));
                    out.println("    </p>");
                }
            }
            out.println("    <hr/>");
            out.println("    <font size='-1' align='center'>" + HtmlReferenceWriter.COPYWRITE + "</font>");
            out.println("	 </body>");
            out.println("  </html>");

            out.close();
        }

    }

}

