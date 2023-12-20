package org.oddjob.doc.taglet;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.UnknownInlineTagTree;
import org.junit.Test;
import org.mockito.Mockito;

import javax.lang.model.element.Element;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PlainTextResourceTagletTest {

	// Test file created on windows
	static final String LS = "\r\n";

    @Test
	public void testProcessTag() {

		DocTree content = mock(DocTree.class);
		when(content.toString()).thenReturn("org/oddjob/doc/loader/SomePlainText.txt");

		UnknownInlineTagTree tag = mock(UnknownInlineTagTree.class);
		Mockito.doReturn(List.of(content)).when(tag).getContent();
		Mockito.when(tag.getTagName()).thenReturn("oddjob.text.resource");
		
		PlainTextResourceTaglet test = new PlainTextResourceTaglet();

		Element element = mock(Element.class);

		String result = test.toString(List.of(tag), element);

		String expected = 
				"<pre>\n" +
				"Remember 2 < 3 & 5 > 4" + LS +
				"But This is a new line." + LS +
				"</pre>\n";
		
		assertThat(result, is(expected));
	}
}
