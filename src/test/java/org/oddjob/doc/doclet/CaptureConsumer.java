package org.oddjob.doc.doclet;

import org.oddjob.doc.beandoc.BeanDocConsumer;
import org.oddjob.doc.beandoc.TypeConsumers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaptureConsumer implements BeanDocConsumer {

    private final StringBuilder firstSentence = new StringBuilder();

    private final StringBuilder body = new StringBuilder();

    private boolean closed;

    public String getFirstSentence() {
        return firstSentence.toString();
    }

    public String getBody() {
        return body.toString();
    }

    public boolean isClosed() {
        return closed;
    }

    @Override
    public void acceptFirstSentence(String text) {
        if (closed) {
            throw new IllegalStateException();
        }

        firstSentence.append(text);
    }

    @Override
    public void acceptBodyText(String text) {
        body.append(text);
    }

    @Override
    public void close() {
        this.closed = true;
    }

    public static class Type implements TypeConsumers {

        private CaptureConsumer description = new CaptureConsumer();

        private Map<String, CaptureConsumer.Property> propertyMap = new HashMap<>();

        private List<CaptureConsumer> examples = new ArrayList<>();

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
