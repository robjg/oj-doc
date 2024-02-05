package org.oddjob.doc.doclet;

import org.oddjob.arooa.beandocs.element.BeanDocElement;
import org.oddjob.doc.beandoc.BeanDocConsumer;
import org.oddjob.doc.beandoc.TypeConsumers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaptureConsumer implements BeanDocConsumer {

    private final List<BeanDocElement> firstSentence = new ArrayList<>();

    private final List<BeanDocElement> body = new ArrayList<>();

    private boolean closed;

    public static Type forType() {
        return new Type();
    }

    public List<BeanDocElement> getFirstSentence() {
        return firstSentence;
    }

    public List<BeanDocElement> getBody() {
        return body;
    }

    public boolean isClosed() {
        return closed;
    }

    @Override
    public void acceptFirstSentence(BeanDocElement element) {
        if (closed) {
            throw new IllegalStateException();
        }

        firstSentence.add(element);
    }

    @Override
    public void acceptBodyText(BeanDocElement element) {
        body.add(element);
    }

    @Override
    public void close() {
        this.closed = true;
    }

    public static class Type implements TypeConsumers {

        private final CaptureConsumer description = new CaptureConsumer();

        private final Map<String, CaptureConsumer.Property> propertyMap = new HashMap<>();

        private final List<CaptureConsumer> examples = new ArrayList<>();

        private boolean closed;

        @Override
        public CaptureConsumer description() {
            return description;
        }

        @Override
        public CaptureConsumer example() {
            CaptureConsumer exampleConsumer = new CaptureConsumer();
            examples.add(exampleConsumer);
            return exampleConsumer;
        }

        @Override
        public CaptureConsumer.Property property(String property) {
            CaptureConsumer.Property propertyConsumer = new CaptureConsumer.Property();
            propertyMap.put(property, propertyConsumer);
            return propertyConsumer;
        }

        public CaptureConsumer getExample(int index) {
            return examples.get(index);
        }

        public CaptureConsumer.Property getProperty(String name) {
            return propertyMap.get(name);
        }

        @Override
        public void close() {
            this.closed = true;
        }

    }

    public static class Property extends CaptureConsumer implements BeanDocConsumer.Property {

        private String required;

        @Override
        public void required(String text) {
            required = text;
        }

        public String getRequired() {
            return required;
        }
    }

}
