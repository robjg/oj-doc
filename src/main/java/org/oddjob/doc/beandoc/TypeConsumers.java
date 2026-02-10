package org.oddjob.doc.beandoc;

import org.oddjob.arooa.convert.doc.MethodIdentifier;

/**
 * Provides Document Consumers for the different parts of an Oddjob documented class.
 */
public interface TypeConsumers extends AutoCloseable {

    BeanDocConsumer description();

    BeanDocConsumer example();

    BeanDocConsumer.Property property(String property);

    BeanDocConsumer conversion();

    BeanDocConsumer conversion(MethodIdentifier methodIdentifier);

    @Override
    void close();
}
