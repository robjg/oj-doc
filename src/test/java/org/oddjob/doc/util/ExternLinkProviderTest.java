package org.oddjob.doc.util;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ExternLinkProviderTest {

    @Test
    void relativeLinkResolvedOk() {

        Path path = Paths.get("./src/test/resources/ref");

        ExternLinkProvider provider = ExternLinkProvider.throwingException();
        provider.addRelativeLink("../api", path);

        LinkResolver linkResolver = provider.apiLinkFor(".");

        String link = linkResolver.resolve("org.foo.stuff.SomeFoo", "html")
                .orElseThrow();

        assertThat(link, is("./../api/org/foo/stuff/SomeFoo.html"));
    }

    @Test
    void urlResolvedOk() throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);

        String htmlResponse = "org.bar\n" +
                "org.bar.stuff\n";

        server.createContext("/bardoc",
                httpExchange -> {
                    httpExchange.sendResponseHeaders(200, htmlResponse.length());
                    try (OutputStream outputStream = httpExchange.getResponseBody()) {
                        outputStream.write(htmlResponse.getBytes());
                    }
                });
        server.start();

        ExternLinkProvider linkProvider = ExternLinkProvider.throwingException();

        String url = "http://localhost:" + server.getAddress().getPort() + "/bardoc";
        linkProvider.addLink(url);

        server.stop(0);

        LinkResolver linkResolver = linkProvider.apiLinkFor("ignored");
        String link = linkResolver.resolve("org.bar.SomeBar", "html")
                .orElseThrow();

        assertThat(link, is(url + "/org/bar/SomeBar.html"));
    }

    @Test
    void elementList404ResolvedOk() throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);

        String htmlResponse = "org.bar\n" +
                "org.bar.stuff\n" +
                "org.bar\n";

        server.createContext("/bardoc",
                httpExchange -> {
                    if (httpExchange.getRequestURI().toString().contains("element-list")) {
                        httpExchange.sendResponseHeaders(404, 0);
                    } else {
                        httpExchange.sendResponseHeaders(200, htmlResponse.length());
                        try (OutputStream outputStream = httpExchange.getResponseBody()) {
                            outputStream.write(htmlResponse.getBytes());
                        }
                    }
                });
        server.start();

        ExternLinkProvider linkProvider = ExternLinkProvider.throwingException();

        String url = "http://localhost:" + server.getAddress().getPort() + "/bardoc";
        linkProvider.addLink(url);

        server.stop(0);

        LinkResolver linkResolver = linkProvider.apiLinkFor("ignored");
        String link = linkResolver.resolve("org.bar.SomeBar", "html")
                .orElseThrow();

        assertThat(link, is(url + "/org/bar/SomeBar.html"));
    }

    @Test
    void elementListHtmlResolvedOk() throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);

        String htmlResponse1 = "<title>Some Html</title";

        String htmlResponse2 = "org.bar\n" +
                "org.bar.stuff\n" +
                "org.bar\n";

        server.createContext("/bardoc",
                httpExchange -> {
                    if (httpExchange.getRequestURI().toString().contains("element-list")) {
                        httpExchange.sendResponseHeaders(200, htmlResponse1.length());
                        try (OutputStream outputStream = httpExchange.getResponseBody()) {
                            outputStream.write(htmlResponse1.getBytes());
                        }
                    } else {
                        httpExchange.sendResponseHeaders(200, htmlResponse2.length());
                        try (OutputStream outputStream = httpExchange.getResponseBody()) {
                            outputStream.write(htmlResponse2.getBytes());
                        }
                    }
                });
        server.start();

        ExternLinkProvider linkProvider = ExternLinkProvider.throwingException();

        String url = "http://localhost:" + server.getAddress().getPort() + "/bardoc";
        linkProvider.addLink(url);

        server.stop(0);

        LinkResolver linkResolver = linkProvider.apiLinkFor("ignored");
        String link = linkResolver.resolve("org.bar.SomeBar", "html")
                .orElseThrow();

        assertThat(link, is(url + "/org/bar/SomeBar.html"));
    }

    @Test
    void moduleElementListLinkResolvedOk() {


        ExternLinkProvider provider = ExternLinkProvider.throwingException();
        provider.addRelativeLink("src/test/resources/moduleapi", Path.of("."));

        LinkResolver linkResolver = provider.apiLinkFor(".");

        String link1 = linkResolver.resolve("java.lang.String", "html")
                .orElseThrow();

        assertThat(link1, is("./src/test/resources/moduleapi/java.base/java/lang/String.html"));

        String link2 = linkResolver.resolve("javax.naming.Foo", "html")
                .orElseThrow();

        assertThat(link2, is("./src/test/resources/moduleapi/java.naming/javax/naming/Foo.html"));
    }


}