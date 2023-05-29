/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.doc.doclet;

import jdk.javadoc.doclet.Reporter;
import org.oddjob.arooa.beandocs.BeanDoc;
import org.oddjob.arooa.beandocs.WriteableBeanDoc;
import org.oddjob.doc.beandoc.BeanDocCollector;
import org.oddjob.doc.util.DocUtil;

import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.List;


/**
 * This class archives away the page data so that it may
 * be retrieved later.
 * 
 * @author Rob Gordon.
 */
public class Archiver {

	private final JobsAndTypes jats;

	private final ElementProcessor elementProcessor;

	private final Reporter reporter;
    public Archiver(JobsAndTypes jats,
					ElementProcessor elementProcessor,
					Reporter reporter) {
    	this.jats = jats;
		this.elementProcessor = elementProcessor;
		this.reporter = reporter;

    }
    
    public void archive(TypeElement element) {

		String fqcn = DocUtil.fqcnFor(element);

    	WriteableBeanDoc beanDoc = jats.docFor(fqcn);

    	if (beanDoc == null) {
    		return;
    	}

		reporter.print(Diagnostic.Kind.NOTE, "Processing " + element);

		elementProcessor.process(element, new BeanDocCollector(beanDoc));

    }
    
    /**
     * Jobs in index order.
     * 
     * @return An array of PageData objects.
     */
    public IndexLine[] getJobData() {
    	List<IndexLine> lines = new ArrayList<>();
    	for (String name : jats.jobs()) {
    		BeanDoc beanDoc = jats.docForJob(name);
    		lines.add(new IndexLine(beanDoc.getClassName(), name,
    				beanDoc.getFirstSentence()));
    	}
    	return lines.toArray(new IndexLine[0]);
    }
    
    /**
     * Types in index order.
     * 
     * @return An array of PageData objects
     */
    public IndexLine[] getTypeData() {
    	List<IndexLine> lines = new ArrayList<>();
    	for (String name : jats.types()) {
    		BeanDoc beanDoc = jats.docForType(name);
    		lines.add(new IndexLine(beanDoc.getClassName(), name,
    				beanDoc.getFirstSentence()));
    	}
    	return lines.toArray(new IndexLine[0]);
    }
    
    public Iterable<? extends BeanDoc> getAll() {
    	return jats.all();
    }


}
