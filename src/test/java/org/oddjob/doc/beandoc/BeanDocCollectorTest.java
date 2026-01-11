package org.oddjob.doc.beandoc;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.beandocs.WriteableConversionDoc;
import org.oddjob.arooa.beandocs.WriteableConversionDocs;
import org.oddjob.arooa.convert.ClassOrMethod;
import org.oddjob.doc.doclet.Conversions;

import java.lang.reflect.Method;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

class BeanDocCollectorTest {

    @Test
    void conversions() throws NoSuchMethodException {

        Method method = BeanDocCollectorTest.class.getDeclaredMethod("conversions");

        String methodName = BeanDocCollectorTest.class.getDeclaredMethod("conversions")
                .getName();

        WriteableConversionDoc conversionDoc = new WriteableConversionDoc();
        conversionDoc.setTypeOrMethod(ClassOrMethod.ofMethod(method).getName());
        conversionDoc.setFromType(Number.class.getTypeName());

        WriteableConversionDocs conversionsByType = new WriteableConversionDocs();
        conversionsByType.add(ClassOrMethod.ofMethod(method), conversionDoc);

        Conversions conversions = new Conversions(conversionsByType);

        Conversions.As as = conversions.docByType(BeanDocCollectorTest.class.getTypeName());

        assertThat(as, notNullValue());

        BeanDocCollector test = new BeanDocCollector(null, as,
                message -> { throw new RuntimeException("Unexpected"); });

        assertThat(test.conversion(methodName), notNullValue());

        test.close();
    }

}