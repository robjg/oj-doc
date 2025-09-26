package org.oddjob.doc.markdown;

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
 * Writes a single Markdown Reference Page.
 */
public class MdPageWriter {

    private final String title;

    private final Path rootDirectory;

    private final MdContextProvider contextProvider;

    public MdPageWriter(String title, Path rootDirectory, MdContextProvider contextProvider) {
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
        return className.replace('.', '/') + ".md";
    }

    /**
     * Get the relative root directory.
     *
     * @return The directory path to the root.
     */
    static String getIndexFile(String className) {

        StringBuilder path = new StringBuilder();
        int start = 0;
        while ((start = className.indexOf('.', start) + 1) > 0) {
            path.append("../");
        }
        return path + "README.md";
    }

    public String getTitle() {
        return title;
    }

    public Path getRootDirectory() {
        return rootDirectory;
    }

    public MdContextProvider getContextProvider() {
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

        MdContext htmlContext = contextProvider.contextFor(pathToRoot);

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

    static class PageWriter {

        private final MdContext mdContext;

        PageWriter(MdContext mdContext) {
            this.mdContext = mdContext;
        }

        String toSection(List<? extends BeanDocElement> elements) {
            return MdVisitor.visitAsSection(elements, mdContext);
        }

        String toLine(List<? extends BeanDocElement> elements) {
            return MdVisitor.visitAsLine(elements, mdContext);
        }

        public void writePageTo(BeanDoc beanDoc, PrintWriter out) {

            out.println("[HOME](" + getIndexFile(beanDoc.getClassName()) + ")");
            out.println("# " + beanDoc.getName());
            out.println();
            out.println(toSection(beanDoc.getAllText()));

            PropertyDoc[] propertyDocs = beanDoc.getPropertyDocs();

            if (propertyDocs.length > 0) {
                out.println();
                out.println("### Property Summary");
                out.println();
                out.println("| Property | Description |");
                out.println("| -------- | ----------- |");
                for (PropertyDoc elem : propertyDocs) {
                    if (ConfiguredHow.HIDDEN == elem.getConfiguredHow()) {
                        continue;
                    }
                    out.println("| [" + elem.getPropertyName() + "](#property" +
                            elem.getPropertyName().toLowerCase() + ") | " // GitHub doesn't like CamelCase.
                            + toLine(elem.getFirstSentence()) + " | ");
                }
                out.println();
            }

            ExampleDoc[] exampleDocs = beanDoc.getExampleDocs();

            if (exampleDocs.length > 0) {
                out.println();
                out.println("### Example Summary");
                out.println();
                out.println("| Title | Description |");
                out.println("| ----- | ----------- |");
                int i = 0;
                for (ExampleDoc elem : exampleDocs) {
                    out.println("| [Example " + ++i + "](#example" + i + ") | " +
                            toLine(elem.getFirstSentence()) + " |" );
                }
                out.println();
            }

            if (propertyDocs.length > 0) {
                out.println();
                out.println("### Property Detail");
                for (PropertyDoc elem : propertyDocs) {
                    if (ConfiguredHow.HIDDEN == elem.getConfiguredHow()) {
                        continue;
                    }
                    out.println("#### " + elem.getPropertyName() + " <a name=\"property"
                            + elem.getPropertyName().toLowerCase() + "\"></a>");
                    out.println();
                    out.println("<table style='font-size:smaller'>");
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
                    out.println("</table>");
                    out.println();
                    out.println(toSection(elem.getAllText()));
                    out.println();
                }
            }

            if (exampleDocs.length > 0) {
                out.println();
                out.println("### Examples");
                int i = 0;
                for (ExampleDoc example : exampleDocs) {
                    out.println("#### Example " + ++i + " <a name=\"example" + i + "\"></a>");
                    out.println();
                    out.println(toSection(example.getAllText()));
                    out.println();
                }
            }

            MdReferenceWriter.footer(out);

            out.close();
        }

    }

}

