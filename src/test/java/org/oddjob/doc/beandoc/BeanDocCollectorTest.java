package org.oddjob.doc.beandoc;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.beandocs.WriteableConversionDoc;
import org.oddjob.arooa.beandocs.WriteableConversionDocs;
import org.oddjob.arooa.convert.ClassOrMethod;
import org.oddjob.arooa.convert.doc.ConversionItemAccess;
import org.oddjob.doc.doclet.Conversions;

import java.lang.reflect.Method;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BeanDocCollectorTest {

    @Test
    void conversions() throws NoSuchMethodException {

        Method method = BeanDocCollectorTest.class.getDeclaredMethod("conversions");

        String methodName = BeanDocCollectorTest.class.getDeclaredMethod("conversions")
                .getName();

        WriteableConversionDoc conversionDoc = new WriteableConversionDoc();
        conversionDoc.setTypeOrMethod(ClassOrMethod.ofMethod(method).getName());
        conversionDoc.setFromType(Number.class.getTypeName());

        ConversionItemAccess<WriteableConversionDoc> itemAccess = mock(ConversionItemAccess.class);
        when(itemAccess.containsForType(BeanDocCollectorTest.class.getCanonicalName()))
                .thenReturn(true);
        when(itemAccess.getForMethod(BeanDocCollectorTest.class.getCanonicalName(), methodName))
                .thenReturn(conversionDoc);

        WriteableConversionDocs conversionsDocs = new WriteableConversionDocs(itemAccess);

        Conversions conversions = new Conversions(conversionsDocs);

        Conversions.As as = conversions.docByType(BeanDocCollectorTest.class.getTypeName());

        assertThat(as, notNullValue());

        BeanDocCollector test = new BeanDocCollector(null, as,
                message -> { throw new RuntimeException("Unexpected"); });

        assertThat(test.conversion(methodName), notNullValue());

        test.close();
    }

}