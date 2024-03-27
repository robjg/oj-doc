/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.doc.doclet;

import jdk.javadoc.doclet.Reporter;
import org.oddjob.arooa.beandocs.BeanDoc;
import org.oddjob.arooa.beandocs.BeanDocArchive;
import org.oddjob.arooa.beandocs.WriteableBeanDoc;
import org.oddjob.doc.beandoc.BeanDocCollector;
import org.oddjob.doc.util.DocUtil;

import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * This class archives away the page data so that it may
 * be retrieved later.
 * 
 * @author Rob Gordon.
 */
public class Archiver implements BeanDocArchive {

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

	@Override
	public Optional<BeanDoc> docFor(String fqn) {
		return Optional.ofNullable(jats.docFor(fqn));
	}

	/**
     * Job Doc in index order.
     * 
     * @return A List of BeanDoc.
     */
	@Override
    public List<BeanDoc> allJobDoc() {
    	List<BeanDoc> docs = new ArrayList<>();
    	for (String name : jats.jobs()) {
    		BeanDoc beanDoc = jats.docForJob(name);
    		docs.add(beanDoc);
    	}
    	return docs;
    }

    /**
     * Type Doc in index order.
     *
	 * @return A List of BeanDoc.
     */
	@Override
    public List<BeanDoc> allTypeDoc() {
    	List<BeanDoc> docs = new ArrayList<>();
    	for (String name : jats.types()) {
    		BeanDoc beanDoc = jats.docForType(name);
    		docs.add(beanDoc);
    	}
    	return docs;
    }
    
    public Iterable<? extends BeanDoc> getAll() {
    	return jats.all();
    }

	@Override
	public String toString() {
		return "Archiver{" +
				jats +
				'}';
	}
}
