package org.oddjob.doc.beandoc;

/**
 * Consumes text processed from the Oddjob Tags in Javadoc.
 */
public interface BeanDocConsumer extends AutoCloseable {

    /**
     * Accept in chunks the first sentence of the documentation.
     *
     * @param text The next part of the first sentence.
     */
    void acceptFirstSentence(String text);

    /**
     * Accept in chunks the full body of the documentation including the first sentence again.
     *
     * @param text The next part of the full body.
     */
    void acceptBodyText(String text);

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
