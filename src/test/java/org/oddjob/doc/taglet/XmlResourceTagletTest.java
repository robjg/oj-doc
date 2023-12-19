package org.oddjob.doc.taglet;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.UnknownInlineTagTree;
import org.junit.Test;
import org.mockito.Mockito;
import org.oddjob.tools.OddjobTestHelper;

import javax.lang.model.element.Element;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class XmlResourceTagletTest {

    @Test
    public void testProcessTag() {

        DocTree content = mock(DocTree.class);
        when(content.toString()).thenReturn("org/oddjob/tools/doclet/utils/SomeXML.xml");

        UnknownInlineTagTree tag = mock(UnknownInlineTagTree.class);
        Mockito.doReturn(List.of(content)).when(tag).getContent();
        Mockito.when(tag.getTagName()).thenReturn("oddjob.xml.resource");

        Element element = mock(Element.class);

        XmlResourceTaglet test = new XmlResourceTaglet();

        String result = test.toString(List.of(tag), element);

        assertEquals("<pre class=\"xml\">" + OddjobTestHelper.LS +
                        "&lt;hello/&gt;</pre>" + OddjobTestHelper.LS,
                result);
    }
}
