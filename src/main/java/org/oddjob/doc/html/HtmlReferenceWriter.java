/*
 * Copyright (c) 2005, Rob Gordon.
 */
package org.oddjob.doc.html;

import org.oddjob.arooa.beandocs.BeanDoc;
import org.oddjob.arooa.beandocs.BeanDocArchive;
import org.oddjob.doc.doclet.IndexLine;
import org.oddjob.doc.doclet.ReferenceWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Creates the reference files.
 *
 * @author Rob Gordon.
 */
public class HtmlReferenceWriter implements ReferenceWriter {

    public static final String COPYWRITE = "(c) R Gordon Ltd 2005 - Present";

    private final HtmlPageWriter pageWriter;
    public HtmlReferenceWriter(Path directory, String title, HtmlContextProvider contextProvider) {
        this.pageWriter = new HtmlPageWriter(
                Objects.requireNonNullElse(title, "Oddjob Reference"),
                directory,
                contextProvider);
    }


    /**
     * Write a single reference page.
     *
     * @param beanDoc
     */
    public IndexLine writePage(BeanDoc beanDoc) {

        return pageWriter.writePage(beanDoc);
    }

    /**
     * Create the index.
     *
     * @param jobs Array of Index Lines for the jobs
     * @param types Array of Index Lines for the types.
     */
    public void writeIndex(List<? extends IndexLine> jobs,
                           List<? extends IndexLine> types) {

        Path directory = pageWriter.getRootDirectory();

        Path indexFile = directory.resolve("index.html");

        try (PrintWriter out = new PrintWriter(new FileWriter(indexFile.toFile()))) {

            writeIndex(jobs, types, out);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create the index.
     *
     * @param jobs Array of Index Lines for the jobs
     * @param types Array of Index Lines for the types.
     * @param out The writer.
     */
    public void writeIndex(List<? extends IndexLine> jobs,
                           List<? extends IndexLine> types,
                           PrintWriter out) {

        String title = pageWriter.getTitle();

        HtmlContext htmlContext = pageWriter.getContextProvider().contextFor(".");

        out.println("<html>");
        out.println("  <head>");
        out.println("  [<a href=\"../index.html\">Home</a>]");
        out.println("    <title>" + title + " - Contents</title>");
        out.println("  </head>");
        out.println("  <body>");
        out.println("    <h2>" + title + "</h2>");
        out.println("    <ul>");
        out.println("    <li>Jobs");
        out.println("      <ul>");
        for (IndexLine indexLine : jobs) {
            out.println("        <li>");
            out.println("          <a href='" + indexLine.getFileName()
                    + "'>" + indexLine.getName() +
                    "</a> - " + HtmlVisitor.visitAll(indexLine.getFirstSentence(), htmlContext));
            out.println("        </li>");
        }
        out.println("      </ul></li>");
        out.println("    <li>Types");
        out.println("      <ul>");
        for (IndexLine indexLine : types) {
            out.println("        <li>");
            out.println("          <a href='" + indexLine.getFileName()
                    + "'>" + indexLine.getName() +
                    "</a> - " + HtmlVisitor.visitAll(indexLine.getFirstSentence(), htmlContext));
            out.println("        </li>");
        }
        out.println("      </ul></li>");
        out.println("    </ul>");

        out.println("    <hr/>");
        out.println("    <font size='-1' align='center'>" + COPYWRITE + "</font>");
        out.println("	 </body>");
        out.println("  </html>");

        out.close();
    }

    public List<IndexLine> writeAll(List<? extends BeanDoc> all) {
        List<IndexLine> indexLines = new ArrayList<>(all.size());

        for (BeanDoc beanDoc : all) {
            indexLines.add(writePage(beanDoc));
        }

        return  indexLines;
    }

    @Override
    public void createManual(BeanDocArchive archive) {

        List<IndexLine> jobIndexLines = writeAll(archive.allJobDoc());
        List<IndexLine> typeIndexLines = writeAll(archive.allTypeDoc());

        writeIndex(jobIndexLines, typeIndexLines);
    }

}
