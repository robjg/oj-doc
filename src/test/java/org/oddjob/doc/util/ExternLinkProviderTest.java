package org.oddjob.doc.util;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
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
    void relativeLinkResolvedOk() throws IOException {

        Path path = Paths.get("./src/test/resources/ref");
        System.out.println(path.toUri());

        ExternLinkProvider provider = ExternLinkProvider.throwingException();
        provider.addRelativeLink("../api", path);

        LinkResolver linkResolver = provider.apiLinkFor(".");

        String link = linkResolver.resolve("org.foo.stuff.SomeFoo", "html");

        assertThat(link, is("./../api/org/foo/stuff/SomeFoo.html"));
    }

    @Test
    void rulResolvedOk() throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);

        String htmlResponse = "org.bar\norg.bar.stuff\n";

        server.createContext("/bardoc", new HttpHandler() {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
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
        String link = linkResolver.resolve("org.bar.SomeBar", "html");

        assertThat(link, is(url + "/org/bar/SomeBar.html"));
    }

}