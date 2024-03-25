package org.oddjob.doc.util;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DocUtilTest {

    @Test
    void relativePath() {

        MatcherAssert.assertThat(DocUtil.relativePath(
                        DocTestUtil.element("aa.bb.cc.X"), "ss.tt.Y"),
                is("../../../ss/tt"));
        assertThat(DocUtil.relativePath(
                        DocTestUtil.element("aa.bb.cc.X.foo"), "aa.bb.oo.Y"),
                is("../../oo"));
        assertThat(DocUtil.relativePath(
                        DocTestUtil.element("aa.bb.cc.X"), "aa.bb.cc.Y"),
                is(""));

    }

    @Test
    void simpleName() {

        assertThat(DocUtil.simpleName("Foo"), is("Foo"));
        assertThat(DocUtil.simpleName("aaa.bbb.Foo"), is("Foo"));
    }

    @Test
    void packageName() {

        assertThat(DocUtil.packageName("Foo"), is(""));
        assertThat(DocUtil.packageName("aaa.bbb.Foo"), is("aaa.bbb"));
    }

    @Test
    void fileName() {

        assertThat(DocUtil.fileNameFor("Foo", "html"), is("Foo.html"));
        assertThat(DocUtil.fileNameFor("aaa.bbb.Foo", "html"), is("aaa/bbb/Foo.html"));
    }


}