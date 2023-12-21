package org.oddjob.doc.beandoc;

import org.oddjob.arooa.beandocs.element.BeanDocElement;

/**
 * Consumes text processed from the Oddjob Tags in Javadoc.
 */
public interface BeanDocConsumer extends AutoCloseable {

    /**
     * Accept in chunks the first sentence of the documentation.
     *
     * @param element The next part of the first sentence.
     */
    void acceptFirstSentence(BeanDocElement element);

    /**
     * Accept in chunks the full body of the documentation including the first sentence again.
     *
     * @param element The next part of the full body.
     */
    void acceptBodyText(BeanDocElement element);

    /**
     * Called when no more documentation is to be consumed.
     */
    void close();

    /**
     * A Consumer for a property as it must also cope with the required tag.
     */
    interface Property extends BeanDocConsumer {

        /**
         * Set the required text. This is all the text as it is not expected to contain inline tags.
         *
         * @param text The required text.
         */
        void required(String text);
    }
}
