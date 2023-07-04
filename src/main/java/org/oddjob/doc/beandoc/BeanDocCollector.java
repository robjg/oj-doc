package org.oddjob.doc.beandoc;

import org.oddjob.arooa.beandocs.WriteableBeanDoc;
import org.oddjob.arooa.beandocs.WriteableExampleDoc;
import org.oddjob.arooa.beandocs.WriteablePropertyDoc;

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

        StringBuilder firstSentence = new StringBuilder();

        StringBuilder description = new StringBuilder();

        return new BeanDocConsumer() {
            @Override
            public void acceptFirstSentence(String text) {
                firstSentence.append(text);
            }

            @Override
            public void acceptBodyText(String text) {
                description.append(text);
            }

            @Override
            public void close() {
                beanDoc.setFirstSentence(firstSentence.toString());
                beanDoc.setAllText(description.toString());
            }

        };
    }

    @Override
    public BeanDocConsumer example() {

        WriteableExampleDoc exampleDoc = new WriteableExampleDoc();

        StringBuilder firstSentence = new StringBuilder();

        StringBuilder exampleDescription = new StringBuilder();

        return new BeanDocConsumer() {

            @Override
            public void acceptFirstSentence(String text) {
                firstSentence.append(text);
            }

            @Override
            public void acceptBodyText(String text) {
                exampleDescription.append(text);
            }

            @Override
            public void close() {
                exampleDoc.setFirstSentence(firstSentence.toString());
                exampleDoc.setAllText(exampleDescription.toString());
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

        StringBuilder firstSentence = new StringBuilder();
        StringBuilder propertyDescription = new StringBuilder();

        return new BeanDocConsumer.Property() {

            @Override
            public void required(String text) {
                writeablePropertyDoc.setRequired(text);
            }

            @Override
            public void acceptFirstSentence(String text) {
                firstSentence.append(text);
            }

            @Override
            public void acceptBodyText(String text) {
                propertyDescription.append(text);
            }

            @Override
            public void close() {
                writeablePropertyDoc.setFirstSentence(firstSentence.toString());
                writeablePropertyDoc.setAllText(propertyDescription.toString());
                beanDoc.addPropertyDoc(writeablePropertyDoc);
            }
        };
    }

    @Override
    public void close() {

    }
}
