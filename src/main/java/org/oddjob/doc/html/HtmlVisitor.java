package org.oddjob.doc.html;

import org.oddjob.arooa.beandocs.element.*;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;

/**
 * Visit Elements and convert them to HTML.
 */
public class HtmlVisitor implements DocElementVisitor<HtmlContext, String> {

    public static HtmlVisitor instance() {
        return new HtmlVisitor();
    }

    public static String visitAll(List<? extends BeanDocElement> elements, HtmlContext htmlContext) {

        HtmlVisitor htmlVisitor = HtmlVisitor.instance();

        StringBuilder builder = new StringBuilder();
        for (BeanDocElement element : elements) {
            builder.append(element.accept(htmlVisitor, htmlContext));
        }
        return builder.toString();
    }

    @Override
    public String visitInternalLink(LinkElement element, HtmlContext context) {

        return context.hyperlinkFor(element);
    }

    @Override
    public String visitPreformattedBlock(PreformattedBlock element, HtmlContext context) {
        return PlainTextToHtml.toHtml(element);
    }

    @Override
    public String visitJavaCodeBlock(JavaCodeBlock element, HtmlContext context) {
        try {
            return JavaToHtml.toHtml(element);
        } catch (IOException e) {
            return visitException(ExceptionElement.of(e), context);
        }
    }

    @Override
    public String visitXmlBlock(XmlBlock element, HtmlContext context) {
        try {
            return XmlToHtml.toHtml(element);
        } catch (TransformerException e) {
            return visitException(ExceptionElement.of(e), context);
        }
    }

    @Override
    public String visitException(ExceptionElement element, HtmlContext context) {
        return HtmlUtil.toHtml(element);
    }

    @Override
    public String visitCode(CodeElement element, HtmlContext context) {
        return HtmlUtil.toHtml(element);
    }

    @Override
    public String visitLiteral(LiteralElement element, HtmlContext context) {
        return HtmlUtil.toHtml(element);
    }
    @Override
    public String visitStandard(StandardElement element, HtmlContext context) {
        return element.getText();
    }

    @Override
    public String visitStartHtmlElement(StartHtmlElement element, HtmlContext context) {
        return element.getText();
    }

    @Override
    public String visitEndHtmlElement(EndHtmlElement element, HtmlContext context) {
        return element.getText();
    }
}
