package org.oddjob.doc.beandoc;

/**
 * Provides Document Consumers for the different parts of an Oddjob documented class.
 */
public interface TypeConsumers extends AutoCloseable {

    BeanDocConsumer description();

    BeanDocConsumer example();

    BeanDocConsumer.Property property(String property);

    @Override
    void close();
}
