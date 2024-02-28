package org.oddjob.doc.markdown;

import org.oddjob.arooa.beandocs.element.*;
import org.oddjob.doc.html.PlainTextToHtml;

import java.util.List;

/**
 * Visit Elements and convert them to Markdown.
 */
public class MdVisitor implements DocElementVisitor<MdContext, String> {

    static class Tag {

        private final String tag;

        Tag(String tag) {
            this.tag = tag;
        }

        boolean is(StartHtmlElement element) {
            return tag.equalsIgnoreCase(element.getName());
        }

        boolean is(EndHtmlElement element) {
            return tag.equalsIgnoreCase(element.getName());
        }
    }

    final static Tag P_TAG = new Tag("P");

    final static Tag UL_TAG = new Tag("UL");

    final static Tag LI_TAG = new Tag("LI");


    private final boolean noNewLines;

    private HtmlHandler htmlHandler = new RootHandler();

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
        return htmlHandler.visitStandard(element);
    }

    @Override
    public String visitStartHtmlElement(StartHtmlElement element, MdContext context) {
        return htmlHandler.startElement(element);
    }

    @Override
    public String visitEndHtmlElement(EndHtmlElement element, MdContext context) {
        return htmlHandler.endElement(element);
    }

    interface HtmlHandler {

        String visitStandard(StandardElement element);

        String startElement(StartHtmlElement element);

        String endElement(EndHtmlElement element);
    }

    class RootHandler implements HtmlHandler {

        @Override
        public String visitStandard(StandardElement element) {
            String text = element.getText();
            if (noNewLines) {
                return removeNewLines(text);
            }
            else {
                return text.replaceAll("[\\t ]*\\r?\\n[\\t ]+", "\n");
            }
        }

        @Override
        public String startElement(StartHtmlElement element) {
            if (P_TAG.is(element)) {
                return "\n";
            }
            else if (UL_TAG.is(element)) {
                htmlHandler = new UnorderedHandler();
                return "";
            }
            else {
                return element.getText();
            }
        }

        @Override
        public String endElement(EndHtmlElement element) {
            if (P_TAG.is(element)) {
                return "\n";
            }
            else {
                return element.getText();
            }
        }
    }

    class UnorderedHandler implements HtmlHandler {

        @Override
        public String visitStandard(StandardElement element) {
            return removeNewLines(element.getText());
        }

        @Override
        public String startElement(StartHtmlElement element) {
            if (LI_TAG.is(element)) {
                return "\n- ";
            }
            else {
                return element.getText();
            }
        }

        @Override
        public String endElement(EndHtmlElement element) {
            if (LI_TAG.is(element)) {
                return "";
            }
            else if (UL_TAG.is(element)) {
                htmlHandler = new RootHandler();
                return "\n";
            }
            else {
                return element.getText();
            }
        }
    }

    public static String removeNewLines(String text) {
        return text.replaceFirst("^\\s*", "")
                .replaceAll("\\s+", " ");
    }
}
