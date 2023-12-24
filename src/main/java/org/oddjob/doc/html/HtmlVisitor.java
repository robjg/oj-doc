package org.oddjob.doc.html;

import org.oddjob.arooa.beandocs.element.*;
import org.oddjob.doc.DocContext;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;

/**
 * Visit Elements and convert them to HTML.
 */
public class HtmlVisitor implements ElementVisitor<DocContext, String> {

    public static HtmlVisitor instance() {
        return new HtmlVisitor();
    }

    public static String visitAll(List<BeanDocElement> elements) {

        HtmlVisitor htmlVisitor = HtmlVisitor.instance();
        DocContext docContext = DocContext.noLinks();

        StringBuilder builder = new StringBuilder();
        for (BeanDocElement element : elements) {
            builder.append(element.accept(htmlVisitor, docContext));
        }
        return builder.toString();
    }

    @Override
    public String visitInternalLink(InternalLink element, DocContext context) {
        return null;
    }

    @Override
    public String visitPreformattedBlock(PreformattedBlock element, DocContext context) {
        return PlainTextToHtml.toHtml(element);
    }

    @Override
    public String visitJavaCodeBlock(JavaCodeBlock element, DocContext context) {
        try {
            return JavaToHtml.toHtml(element);
        } catch (IOException e) {
            return visitException(ExceptionElement.of(e), context);
        }
    }

    @Override
    public String visitXmlBlock(XmlBlock element, DocContext context) {
        try {
            return XmlToHtml.toHtml(element);
        } catch (TransformerException e) {
            return visitException(ExceptionElement.of(e), context);
        }
    }

    @Override
    public String visitException(ExceptionElement element, DocContext context) {
        return HtmlUtil.toHtml(element);
    }

    @Override
    public String visitCode(CodeElement element, DocContext context) {
        return HtmlUtil.toHtml(element);
    }

    @Override
    public String visitLiteral(LiteralElement element, DocContext context) {
        return HtmlUtil.toHtml(element);
    }
    @Override
    public String visitStandard(StandardElement element, DocContext context) {
        return element.getText();
    }
}
