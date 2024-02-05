package org.oddjob.doc.visitor;

import org.oddjob.doc.doclet.ThingWithSomeDoc;
import org.oddjob.doc.doclet.ThingWithSomeDocBase;

/**
 * @oddjob.description Like {@link org.oddjob.doc.doclet.ThingWithSomeDoc} but
 * with links including method link {@link ThingWithSomeDoc#setAnotherProp()} and
 * {@link #someProp With A Label}. A link outside the tree like {@link org.foo.FruitLoop} is also possible as is
 * {@link #badLink A Property That Doesn't Exist}.
 */
@SuppressWarnings("JavadocReference")
public class ThingWithSomeLinks extends ThingWithSomeDocBase {

    /**
     * @oddjob.description A property can also contain a link such as {@link org.oddjob.doc.doclet.ThingWithSomeDoc}.
     */
    String someProp;
}
