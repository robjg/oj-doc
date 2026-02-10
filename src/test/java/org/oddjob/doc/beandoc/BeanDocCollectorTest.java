package org.oddjob.doc.beandoc;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.beandocs.WriteableConversionDoc;
import org.oddjob.arooa.beandocs.WriteableConversionDocs;
import org.oddjob.arooa.convert.doc.ConversionItemAccess;
import org.oddjob.arooa.convert.doc.ElementIdentifier;
import org.oddjob.arooa.convert.doc.MethodIdentifier;
import org.oddjob.arooa.convert.doc.TypeIdentifier;
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
        conversionDoc.setTypeOrMethod(ElementIdentifier.ofMethod(method).getName());
        conversionDoc.setFromType(Number.class.getTypeName());

        ConversionItemAccess<WriteableConversionDoc> itemAccess = mock(ConversionItemAccess.class);
        when(itemAccess.containsForType(TypeIdentifier.ofClass(BeanDocCollectorTest.class)))
                .thenReturn(true);
        when(itemAccess.getForMethod(ElementIdentifier.ofMethod(method)))
                .thenReturn(conversionDoc);

        WriteableConversionDocs conversionsDocs = new WriteableConversionDocs(itemAccess);

        Conversions conversions = new Conversions(conversionsDocs);

        Conversions.As as = conversions.docByType(ElementIdentifier.ofClass(BeanDocCollectorTest.class));

        assertThat(as, notNullValue());

        BeanDocCollector test = new BeanDocCollector(null, as,
                message -> { throw new RuntimeException("Unexpected"); });

        assertThat(test.conversion(MethodIdentifier.ofMethod(method)), notNullValue());

        test.close();
    }

}