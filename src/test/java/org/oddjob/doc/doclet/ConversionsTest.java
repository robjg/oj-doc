package org.oddjob.doc.doclet;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.beandocs.SessionArooaDocFactory;
import org.oddjob.arooa.convert.doc.ElementIdentifier;
import org.oddjob.arooa.standard.StandardArooaSession;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

class ConversionsTest {

    void someMethod() {}

    @Test
    void addAndLookupType() throws ClassNotFoundException, NoSuchMethodException {

        SessionArooaDocFactory docFactory =  new SessionArooaDocFactory(
                new StandardArooaSession());

        Conversions conversions = new Conversions(docFactory.createConversionDocs());

        Class<?> cl = Class.forName("org.oddjob.arooa.convert.convertlets.BooleanConvertlets$NumberToBoolean");

        Conversions.As byTypeDoc = conversions.docByType(
                ElementIdentifier.ofClass(cl));

        assertThat(byTypeDoc, notNullValue());

        assertThat(byTypeDoc.asType(), notNullValue());
        assertThat(byTypeDoc.asMethod(ElementIdentifier
                .ofMethod(getClass().getDeclaredMethod("someMethod"))), nullValue());
    }
}