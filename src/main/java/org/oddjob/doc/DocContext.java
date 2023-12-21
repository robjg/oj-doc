package org.oddjob.doc;

/**
 * A Context used in a Bean Doc Element Visitor.
 */
public interface DocContext {

    static DocContext noLinks() {
        return new DocContext() {
        };
    }
}
