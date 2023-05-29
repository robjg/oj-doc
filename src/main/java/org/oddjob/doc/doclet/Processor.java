/*
 * Copyright (c) 2005, Rob Gordon.
 */
package org.oddjob.doc.doclet;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.util.DocTrees;
import jdk.javadoc.doclet.Reporter;
import org.oddjob.arooa.utils.EtcUtils;
import org.oddjob.doc.beandoc.TypeConsumers;
import org.oddjob.doc.util.InlineTagHelper;
import org.oddjob.doc.visitor.PropertyVisitor;
import org.oddjob.doc.visitor.TypeVisitor;
import org.oddjob.doc.visitor.VisitorContext;
import org.oddjob.doc.visitor.VisitorContextBuilder;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Optional;

/**
 * A Processor is capable of processing a java ClassDoc object into
 * a reference PageData object.
 *   
 * @author Rob Gordon.
 */
public class Processor implements ElementProcessor{


	private final DocTrees docTrees ;

	private final InlineHelperProvider linkProcessorProvider;

	private final Reporter reporter;

	/**
	 * Create a processor.
	 *  
	 * @param docTrees The utility class for access comments.
	 * @param linkProcessorProvider Helper for inline tags.
	 * @param reporter The javadoc Reporter.
	 */
	public Processor(DocTrees docTrees,
					 InlineHelperProvider linkProcessorProvider,
					 Reporter reporter) {

		this.docTrees = docTrees;
		this.linkProcessorProvider = linkProcessorProvider;
		this.reporter = reporter;
	}

	/**
	 * Process a Type Element.
	 *
	 * @param element The Type Element.
	 * @param typeConsumers The Consumer for a Type
	 */
	@Override
	public void process(TypeElement element, TypeConsumers typeConsumers) {

		DocCommentTree docCommentTree = docTrees.getDocCommentTree(element);

		if (docCommentTree == null) {
			return;
		}

		InlineTagHelper inlineTagHelper = linkProcessorProvider.forElement(element);

		VisitorContext visitorContext = VisitorContextBuilder.create(
				inlineTagHelper, reporter, element);

		TypeVisitor.with(docTrees, visitorContext)
				.visit(docCommentTree, typeConsumers);


		List<? extends Element> enclosed = element.getEnclosedElements();

		for (Element e : enclosed) {

			processFieldOrMethod(e, typeConsumers, inlineTagHelper);
		}

		typeConsumers.close();
	}


	/**
	 * Process fields and method elements and ignore others.
	 *
	 * @param element The field or method or other element.
	 * @param beanDocConsumer The thing that provides the consumer for property doc.
	 * @param inlineTagHelper Use to create the new context.
	 */
	void processFieldOrMethod(Element element, TypeConsumers beanDocConsumer,
							  InlineTagHelper inlineTagHelper) {

		Optional<String> optionalPropertyName = toProp(element);

		if (optionalPropertyName.isEmpty()) {
			return;
		}

		DocCommentTree docCommentTree = docTrees.getDocCommentTree(element);

		if (docCommentTree == null) {
			return;
		}

		String propertyName = optionalPropertyName.get();

		VisitorContext visitorContext = VisitorContextBuilder.create(
				inlineTagHelper, reporter, element);

		PropertyVisitor.with(docTrees, visitorContext)
				.visit(docCommentTree, beanDocConsumer, propertyName);

	}

	static Optional<String> toProp(Element element) {

		if (element.getKind() == ElementKind.METHOD) {
			return EtcUtils.propertyFromMethodName(element.getSimpleName().toString());
		}
		else if (element.getKind() == ElementKind.FIELD) {
			return Optional.of(element.getSimpleName().toString());
		}
		else {
			return Optional.empty();
		}
	}

}
