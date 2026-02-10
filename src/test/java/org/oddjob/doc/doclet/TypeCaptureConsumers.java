package org.oddjob.doc.doclet;

import org.oddjob.arooa.convert.doc.MethodIdentifier;
import org.oddjob.doc.beandoc.BeanDocConsumer;
import org.oddjob.doc.beandoc.TypeConsumers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeCaptureConsumers implements TypeConsumers {

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
    public BeanDocConsumer conversion() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BeanDocConsumer conversion(MethodIdentifier methodIdentifier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        this.closed = true;
    }

}
