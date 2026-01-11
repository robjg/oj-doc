package org.oddjob.doc.doclet;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.beandocs.SessionArooaDocFactory;
import org.oddjob.arooa.standard.StandardArooaSession;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

class ConversionsTest {

    @Test
    void addAndLookupType() {

        SessionArooaDocFactory docFactory =  new SessionArooaDocFactory(
                new StandardArooaSession());

        Conversions conversions = new Conversions(docFactory.createConversionDocs());

        Conversions.As byTypeDoc = conversions.docByType(
                "org.oddjob.arooa.convert.convertlets.BooleanConvertlets.NumberToBoolean");

        assertThat(byTypeDoc, notNullValue());

        assertThat(byTypeDoc.asType(), notNullValue());
        assertThat(byTypeDoc.asMethod("someMethod"), nullValue());
    }
}