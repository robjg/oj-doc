package org.oddjob.doc.markdown;

import org.oddjob.arooa.beandocs.element.*;

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


    /** If set then new lines are removed. So for the index README we don't want new
     * lines. */
    private final boolean noNewLines;

    private boolean startSection = true;

    private HtmlHandler htmlHandler = new RootHandler();

    public MdVisitor(boolean noNewLines) {
        this.noNewLines = noNewLines;
    }

    /**
     * Create a visitor that keeps new lines.
     *
     * @return The visitor.
     */
    public static MdVisitor instance() {
        return new MdVisitor(false);
    }

    public static MdVisitor instance(boolean noNewLines) {
        return new MdVisitor(noNewLines);
    }

    public static String visitAsSection(List<? extends BeanDocElement> elements, MdContext mdContext) {

        return doVisit(elements, mdContext, false);
    }

    public static String visitAsLine(List<? extends BeanDocElement> elements,
                                     MdContext mdContext) {
        return doVisit(elements, mdContext, true);
    }

    static String doVisit(List<? extends BeanDocElement> elements,
                   MdContext mdContext,
                   boolean noNewLines) {

        if (elements == null || elements.isEmpty()) {
            return "";
        }

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
        return "```\n" + element.getText().stripTrailing() + "\n```\n";
    }

    @Override
    public String visitJavaCodeBlock(JavaCodeBlock element, MdContext context) {
        return "```java\n" + element.getCode().stripTrailing() + "\n```\n";
    }

    @Override
    public String visitXmlBlock(XmlBlock element, MdContext context) {
        return "```xml\n" + element.getXml().stripTrailing() + "\n```\n";
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
            } else {
                return pruneNewlines(text);
            }
        }

        @Override
        public String startElement(StartHtmlElement element) {
            if (P_TAG.is(element)) {
                return "\n";
            } else if (UL_TAG.is(element)) {
                htmlHandler = new UnorderedHandler();
                return "";
            } else {
                return element.getText();
            }
        }

        @Override
        public String endElement(EndHtmlElement element) {
            if (P_TAG.is(element)) {
                return "\n";
            } else {
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
            } else {
                return element.getText();
            }
        }

        @Override
        public String endElement(EndHtmlElement element) {
            if (LI_TAG.is(element)) {
                // we want the start of any text trimmed as we're really only expecting whitespace up to the
                // next li or /ul tag.
                startSection = true;
                return "";
            } else if (UL_TAG.is(element)) {
                htmlHandler = new RootHandler();
                return "\n";
            } else {
                return element.getText();
            }
        }
    }

    protected String removeNewLines(String text) {
        if (startSection) {
            text = text.replaceFirst("^\\s*", "");
            startSection = false;
        }
        return text.replaceAll("\\s+", " ");
    }

    protected String pruneNewlines(String text) {
        return text.replaceAll("[\\t ]*\\r?\\n[\\t ]+", "\n");
    }

}
