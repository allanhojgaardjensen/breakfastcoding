package com.example;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.grizzly.http.server.HttpServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class GreetingTest {

    private HttpServer server;
    private WebTarget target;

    @Before
    public void setUp() throws Exception {
        server = ServiceExecutor.startServer();
        Client c = ClientBuilder.newClient();
        target = c.target(ServiceExecutor.BASE_URI);
    }

    @After
    public void tearDown() throws Exception {
        server.shutdownNow();
    }

    @Test
    public void testGetGreeting() {
        String responseMsg = target.path("greetings").request().get(String.class);
        assertEquals("{\"greeting\":\"Hallo!\"}", responseMsg);
    }

    @Test
    public void testGetNoFrenchGreeting() {
        String responseMsg = target.path("greetings").request().acceptLanguage("fr").get(String.class);
        assertEquals("{\"greeting\":\"Hallo!\"}", responseMsg);
    }

    @Test
    public void testGetPreferredGreetingDanish() {
        String responseMsg = target.path("greetings").request().acceptLanguage("da, en-gb;q=0.9, en;q=0.8, fr;q=0.5").get(String.class);
        assertEquals("{\"greeting\":\"Hallo!\"}", responseMsg);
    }

    @Test
    public void testGetPreferredGreetingEnglish() {
        String responseMsg = target.path("greetings").request().acceptLanguage("en, da;q=0.9, en-gb;q=0.8, fr;q=0.5").get(String.class);
        assertEquals("{\"greeting\":\"Hello!\"}", responseMsg);
    }

    @Test
    public void testGetPreferredGreetingDanishEnglish() {
        String responseMsg = target.path("greetings").request().acceptLanguage("da, en, da;q=0.9, en-gb;q=0.8, fr;q=0.5").get(String.class);
        assertEquals("{\"greeting\":\"Hallo!\"}", responseMsg);
    }

    @Test
    public void testGetPreferredGreetingEnglishDanish() {
        String responseMsg = target.path("greetings").request().acceptLanguage("en, da, da;q=0.9, en-gb;q=0.8, fr;q=0.5").get(String.class);
        assertEquals("{\"greeting\":\"Hello!\"}", responseMsg);
    }

    @Test
    public void testGetEnglishGreeting() {
        String responseMsg = target.path("greetings").request().acceptLanguage("en").get(String.class);
        assertEquals("{\"greeting\":\"Hello!\"}", responseMsg);
    }

    @Test
    public void testGetEnglishGreetingByBritish() {
        String responseMsg = target.path("greetings").request().acceptLanguage("en-gb").get(String.class);
        assertEquals("{\"greeting\":\"Hello!\"}", responseMsg);
    }
}
