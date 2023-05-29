package org.oddjob.doc.util;

import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DocTestUtil {

    public static TypeElement element(String qualifiedName) {

        TypeElement element = mock(TypeElement.class);
        when(element.getQualifiedName()).thenReturn(new SimpleName(qualifiedName));

        return element;
    }

    static class SimpleName implements Name {

        private final String string;

        SimpleName(String string) {
            this.string = string;
        }

        @Override
        public boolean contentEquals(CharSequence cs) {
            return cs.equals(string);
        }

        @Override
        public int length() {
            return string.length();
        }

        @Override
        public char charAt(int index) {
            return string.charAt(index);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return string.subSequence(start, end);
        }

        @Override
        public String toString() {
            return string;
        }
    }


}
