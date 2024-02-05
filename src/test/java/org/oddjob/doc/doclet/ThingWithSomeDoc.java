package org.oddjob.doc.doclet;

/**
 * The first sentence. Another Sentence.
 * <p>Something in a paragraph</p>
 *
 * @oddjob.description First sentence in block tag. Some more stuff in the block tag. {@link ThingWithSomeDoc}.
 * {@our.inline CustomInline}. <p>And some html</p>.
 *
 * @oddjob.example This is an example.
 *
 * Which can go over several lines.
 *
 * {@our.inline ExampleCode}
 *
 * @see Processor
 */
public class ThingWithSomeDoc extends ThingWithSomeDocBase {

    /**
     * @oddjob.property
     * @oddjob.description Some property
     */
    int someProp;

    /**
     * @oddjob.property
     * @oddjob.description Another property
     * @oddjob.required Yes
     *
     */
    public void setAnotherProp() {

    }
}
