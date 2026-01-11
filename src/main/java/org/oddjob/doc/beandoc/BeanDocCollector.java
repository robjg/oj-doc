package org.oddjob.doc.beandoc;

import org.oddjob.arooa.beandocs.WriteableBeanDoc;
import org.oddjob.arooa.beandocs.WriteableConversionDoc;
import org.oddjob.arooa.beandocs.WriteableExampleDoc;
import org.oddjob.arooa.beandocs.WriteablePropertyDoc;
import org.oddjob.arooa.beandocs.element.BeanDocElement;
import org.oddjob.doc.doclet.Conversions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Wraps {@link WriteableBeanDoc}.
 */
public class BeanDocCollector implements TypeConsumers {

    private final WriteableBeanDoc beanDoc;

    private final Conversions.As conversionAs;

    private final Consumer<? super String> warningHandler;

    public BeanDocCollector(WriteableBeanDoc beanDoc,
                            Conversions.As conversionAs,
                            Consumer<? super String> warningHandler) {
        this.beanDoc = beanDoc;
        this.conversionAs = conversionAs;
        this.warningHandler = warningHandler;
    }

    @Override
    public BeanDocConsumer description() {

        if (beanDoc == null) {
            warningHandler.accept("Conversion Doc only - Description Tag will be ignored");
            return null;
        }

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

        if (beanDoc == null) {
            warningHandler.accept("Conversion Doc only - Example Tag will be ignored");
            return null;
        }

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
    public BeanDocConsumer conversion() {

        WriteableConversionDoc conversionDoc = conversionAs.asType();
        if (conversionDoc == null) {
            warningHandler.accept("Conversion Doc not expected.");
            return null;
        }

        List<BeanDocElement> firstSentence = new ArrayList<>();

        List<BeanDocElement> conversionDescription = new ArrayList<>();

        return new BeanDocConsumer() {
            @Override
            public void acceptFirstSentence(BeanDocElement element) {
                firstSentence.add(element);
            }

            @Override
            public void acceptBodyText(BeanDocElement element) {
                conversionDescription.add(element);
            }

            @Override
            public void close() {
                conversionDoc.setFirstSentence(firstSentence);
                conversionDoc.setAllText(conversionDescription);
            }
        };
    }

    @Override
    public BeanDocConsumer conversion(String method) {

        WriteableConversionDoc conversionDoc = conversionAs.asMethod(method);
        if (conversionDoc == null) {
            warningHandler.accept("Conversion Doc not expected.");
            return null;
        }

        List<BeanDocElement> firstSentence = new ArrayList<>();

        List<BeanDocElement> conversionDescription = new ArrayList<>();

        return new BeanDocConsumer() {
            @Override
            public void acceptFirstSentence(BeanDocElement element) {
                firstSentence.add(element);
            }

            @Override
            public void acceptBodyText(BeanDocElement element) {
                conversionDescription.add(element);
            }

            @Override
            public void close() {
                conversionDoc.setFirstSentence(firstSentence);
                conversionDoc.setAllText(conversionDescription);
            }
        };
    }

    @Override
    public BeanDocConsumer.Property property(String property) {

        if (this.beanDoc == null) {
            return null;
        }

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
