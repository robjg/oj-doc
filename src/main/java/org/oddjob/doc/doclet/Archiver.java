/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.doc.doclet;

import jdk.javadoc.doclet.Reporter;
import org.oddjob.arooa.beandocs.BeanDoc;
import org.oddjob.arooa.beandocs.BeanDocArchive;
import org.oddjob.arooa.beandocs.ConversionArchive;
import org.oddjob.arooa.beandocs.ConversionDoc;

import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 * This class archives away the page data so that it may
 * be retrieved later.
 * 
 * @author Rob Gordon.
 */
public class Archiver implements BeanDocArchive, ConversionArchive {

    private final JobsAndTypes jats;

    private final Conversions conversions;

    private final ElementProcessor elementProcessor;

    private final Reporter reporter;

    public Archiver(JobsAndTypes jats,
                    Conversions conversions,
					ElementProcessor elementProcessor,
					Reporter reporter) {
        this.jats = jats;
        this.conversions = conversions;
		this.elementProcessor = elementProcessor;
        this.reporter = reporter;
    }
    
    public void archive(TypeElement element) {

        TypeConsumersProvider typeConsumersProvider = new ArchiverTypeConsumers(jats, conversions, reporter);

		elementProcessor.process(element, typeConsumersProvider);

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
    public List<ConversionDoc> conversionDocFor(String typeName) {
        return Arrays.stream(conversions.getConversionDocsFrom(typeName))
                .toList();
    }

    @Override
    public List<ConversionDoc> allConversionDoc() {
        return Arrays.stream(conversions.getConversionDocs())
                .toList();
    }

    @Override
	public String toString() {
		return "Archiver{" +
				jats +
				'}';
	}
}
