package org.oddjob.doc.visitor;

/**
 * @oddjob.conversion Provides some conversions.
 *
 */
public class ThingWithConversion {

    /**
     * @oddjob.conversion Provides a conversion to an Integer. This always returns 42 as it is a test.
     *
     * @return 42.
     */
    public Integer toNumber() {
        return 42;
    }
}
