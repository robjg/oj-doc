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

public class PlainTextResourceTagletTest {
		
    @Test
	public void testProcessTag() {

		DocTree content = mock(DocTree.class);
		when(content.toString()).thenReturn("org/oddjob/tools/doclet/utils/SomePlainText.txt");

		UnknownInlineTagTree tag = mock(UnknownInlineTagTree.class);
		Mockito.doReturn(List.of(content)).when(tag).getContent();
		Mockito.when(tag.getTagName()).thenReturn("oddjob.text.resource");
		
		PlainTextResourceTaglet test = new PlainTextResourceTaglet();

		Element element = mock(Element.class);

		String result = test.toString(List.of(tag), element);

		String expected = 
				"<pre>" + OddjobTestHelper.LS +
				"Remember 2 < 3 & 5 > 4" + OddjobTestHelper.LS +
				"But This is a new line." + OddjobTestHelper.LS +
				"</pre>" + OddjobTestHelper.LS;
		
		assertEquals(expected, result);
	}
}
