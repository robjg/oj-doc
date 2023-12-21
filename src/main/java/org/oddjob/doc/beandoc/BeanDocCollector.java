package org.oddjob.doc.beandoc;

import org.oddjob.arooa.beandocs.WriteableBeanDoc;
import org.oddjob.arooa.beandocs.WriteableExampleDoc;
import org.oddjob.arooa.beandocs.WriteablePropertyDoc;
import org.oddjob.arooa.beandocs.element.BeanDocElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Wraps {@link WriteableBeanDoc}.
 */
public class BeanDocCollector implements TypeConsumers {

    private final WriteableBeanDoc beanDoc;

    public BeanDocCollector(WriteableBeanDoc beanDoc) {
        this.beanDoc = beanDoc;
    }

    @Override
    public BeanDocConsumer description() {

        List<BeanDocElement> firstSentence = new ArrayList<>();

        List<BeanDocElement> description = new ArrayList<>();

        return new BeanDocConsumer() {
            @Override
            public void acceptFirstSentence(BeanDocElement element) {
                firstSentence.add(element);
            }

            @Override
            public void acceptBodyText(BeanDocElement element) {
                description.add(element);
            }

            @Override
            public void close() {
                beanDoc.setFirstSentence(firstSentence);
                beanDoc.setAllText(description);
            }

        };
    }

    @Override
    public BeanDocConsumer example() {

        WriteableExampleDoc exampleDoc = new WriteableExampleDoc();

        List<BeanDocElement> firstSentence = new ArrayList<>();

        List<BeanDocElement> exampleDescription = new ArrayList<>();

        return new BeanDocConsumer() {

            @Override
            public void acceptFirstSentence(BeanDocElement element) {
                firstSentence.add(element);
            }

            @Override
            public void acceptBodyText(BeanDocElement element) {
                exampleDescription.add(element);
            }

            @Override
            public void close() {
                exampleDoc.setFirstSentence(firstSentence);
                exampleDoc.setAllText(exampleDescription);
                beanDoc.addExampleDoc(exampleDoc);
            }
        };
    }


    @Override
    public BeanDocConsumer.Property property(String property) {

        WriteablePropertyDoc writeablePropertyDoc = this.beanDoc.propertyDocFor(property);

        // This happens when a base class has some property doc, but
        // it's overridden by a super class (i.e. VariablesJob)
        if (writeablePropertyDoc == null) {
            return null;
        }

        List<BeanDocElement> firstSentence = new ArrayList<>();
        List<BeanDocElement> propertyDescription = new ArrayList<>();

        return new BeanDocConsumer.Property() {

            @Override
            public void required(String text) {
                writeablePropertyDoc.setRequired(text);
            }

            @Override
            public void acceptFirstSentence(BeanDocElement element) {
                firstSentence.add(element);
            }

            @Override
            public void acceptBodyText(BeanDocElement element) {
                propertyDescription.add(element);
            }

            @Override
            public void close() {
                writeablePropertyDoc.setFirstSentence(firstSentence);
                writeablePropertyDoc.setAllText(propertyDescription);
                beanDoc.addPropertyDoc(writeablePropertyDoc);
            }
        };
    }

    @Override
    public void close() {

    }
}
