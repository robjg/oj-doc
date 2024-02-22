package org.oddjob.doc.markdown;

import org.oddjob.arooa.beandocs.element.*;
import org.oddjob.doc.html.PlainTextToHtml;

import java.util.List;

/**
 * Visit Elements and convert them to Markdown.
 */
public class MdVisitor implements DocElementVisitor<MdContext, String> {

    private final boolean noNewLines;

    public MdVisitor(boolean noNewLines) {
        this.noNewLines = noNewLines;
    }

    public static MdVisitor instance() {
        return new MdVisitor(false);
    }

    public static MdVisitor instance(boolean noNewLines) {
        return new MdVisitor(noNewLines);
    }

    public static String visitAll(List<? extends BeanDocElement> elements, MdContext mdContext) {

        return visitAll(elements, mdContext, false);
    }

    public static String visitAll(List<? extends BeanDocElement> elements,
                                  MdContext mdContext,
                                  boolean noNewLines) {

        MdVisitor mdVisitor = MdVisitor.instance(noNewLines);

        StringBuilder builder = new StringBuilder();
        for (BeanDocElement element : elements) {
            builder.append(element.accept(mdVisitor, mdContext));
        }
        return builder.toString();
    }

    @Override
    public String visitInternalLink(LinkElement element, MdContext context) {

        return context.processLink(element);
    }

    @Override
    public String visitPreformattedBlock(PreformattedBlock element, MdContext context) {
        return PlainTextToHtml.toHtml(element);
    }

    @Override
    public String visitJavaCodeBlock(JavaCodeBlock element, MdContext context) {
        return "```java\n" + element.getCode() + "\n```\n";
    }

    @Override
    public String visitXmlBlock(XmlBlock element, MdContext context) {
        return "```xml\n" + element.getXml() + "\n```\n";
    }

    @Override
    public String visitException(ExceptionElement element, MdContext context) {
        return "_" + element.getMessage() + "_";
    }

    @Override
    public String visitCode(CodeElement element, MdContext context) {
        return "`" + element.getText() + "`";
    }

    @Override
    public String visitLiteral(LiteralElement element, MdContext context) {
        return element.getText();
    }

    @Override
    public String visitStandard(StandardElement element, MdContext context) {
        String text = element.getText();
        if (noNewLines) {
            return text.replaceAll("\n(\r)?", " ");
        }
        else {
            return text;
        }
    }
}
