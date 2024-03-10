# Oddjob Documentation Tool

Used to create the Oddjob Reference s. 

The Oddjob source documentation itself is
in the [oj-examples](https://github.com/robjg/oj-examples) repo.

This module contains 
- A Doclet that is able to process bespoke tags in the source
documentation and produce either HTML or Markdown pages.
- Taglets that are able to process the same tags to enhance
the javadoc with such things as the loaded examples.
- A PostProcessor for HTML that is able to parse pages and replace
tags with content loaded from the file system. This is used to
parse the User and Developer documentation allowing all examples
to be Unit Tested.

### Markup Support in Tags

Within the Bespoke block tags the support for standard Javadoc tags
is limited because it proved too challenging to try and include
the default doclet processing so all tags processing with these block
tags needs to also be custom.

The custom block tags are

{@oddjob.description}
: Valid on a Type, member of getter/setter. 

{@oddjob.property}
: Allows a property to be documented with a different name.

{@oddjob.example}
: Valid on a Type. Creates an Example block in the reference.

Supported javadoc tags are:

{@link}
: The contents will be checked against the reference and
if it refers to another job or type in the reference that page will
be linked otherwise an api url will be used. Only one apiurl can be
specified and it defaults to the [rgordon.co.uk api doc](rgordon.co.uk/oddjob/docs.html).
links to the JDK api should not be used in these blocks as they will not be resolved.

{@code}
: This is converted to preformatted text.

Custom tags

{@oddjob.xml.resource}
: The contents will be a classpath resource that will be loaded as XML. 

{@oddjob.java.resource}
: The contents will be a classpath resource that will be loaded as Java.

{@oddjob.text.resource}
: The contents will be a classpath resource that will be loaded as preformatted
plain text.

### Classpaths

The oj-doc module is built with a provided dependency on Arooa. This is so that 
the Post Processor job can be
run from Oddjob (see [oj-assembly](https://github.com/robjg/oj-assembly) for how this happens). Because of 
this when using the Doclet or Taglets outside Oddjob (i.e. in Maven or Ant) then
the Arooa Artifact must be added to the doclet or artifact classpath.


There are three Classpaths to consider when using the Reference Doclet.
- The classpath of the javadoc tool, used to parse the source code
- The docletpath used to load the [ReferenceDoclet](src/main/java/org/oddjob/doc/doclet/ReferenceDoclet.java).
This must include Arooa (so the descriptor can be loaded) and any
provided scope dependencies that the jobs in the descriptor use. 
- The loaderpath used to find examples specified in the above resource tags. This
usually points at the test resources directory.

When creating the Javadoc a custom tagletpath must be specified. This must
point at this module so the taglets may be found, and include Arooa, and also include the test
classes directory so that the resources can be found.

---

Useful links:

https://openjdk.org/groups/compiler/using-new-doclet.html
https://docs.oracle.com/en/java/javase/11/docs/api/jdk.javadoc/module-summary.html

