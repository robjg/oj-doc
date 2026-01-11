package org.oddjob.doc.doclet;

import org.oddjob.arooa.beandocs.element.BeanDocElement;
import org.oddjob.doc.beandoc.BeanDocConsumer;

import java.util.ArrayList;
import java.util.List;

public class CaptureConsumer implements BeanDocConsumer {

    private final List<BeanDocElement> firstSentence = new ArrayList<>();

    private final List<BeanDocElement> body = new ArrayList<>();

    private boolean closed;

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
