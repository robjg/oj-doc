/*
 * Copyright (c) 2005, Rob Gordon.
 */
package org.oddjob.doc.markdown;

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
 * Creates the reference files in Markdown.
 *
 * @author Rob Gordon.
 */
public class MdReferenceWriter implements ReferenceWriter {

    public static final String COPYWRITE = "(c) R Gordon Ltd 2005 - Present";

    private final MdPageWriter pageWriter;

    public MdReferenceWriter(String directory, String title, MdContextProvider contextProvider) {
        this.pageWriter = new MdPageWriter(
                Objects.requireNonNullElse(title, "Oddjob Reference"),
                Path.of(directory),
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

        Path indexFile = directory.resolve("README.md");

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

        MdContext mdContext = pageWriter.getContextProvider().contextFor("./");

        String title = pageWriter.getTitle();

        out.println("# " + title);
        out.println();
        out.println("### Jobs");
        out.println();
        if (jobs.isEmpty()) {
            out.println();
            out.println("> No Jobs Defined.");
            out.println();
        }
        else {
            for (IndexLine indexLine : jobs) {
                out.println("- [" + indexLine.getName() + "](" + indexLine.getFileName()
                        + ") - " + MdVisitor.visitAsLine(indexLine.getFirstSentence(), mdContext));
            }
        }
        out.println();
        out.println("### Types");
        out.println("");
        if (types.isEmpty()) {
            out.println();
            out.println("> No Types Defined.");
            out.println();
        }
        else {
            for (IndexLine indexLine : types) {
                out.println("- [" + indexLine.getName() + "](" + indexLine.getFileName()
                        + ") - " + MdVisitor.visitAsLine(indexLine.getFirstSentence(), mdContext));
            }
        }

        footer(out);

        out.close();
    }

    static void footer(PrintWriter out) {

        out.println();
        out.println("-----------------------");
        out.println();
        out.println("<div style='font-size: smaller; text-align: center;'>" + COPYWRITE + "</div>");
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
