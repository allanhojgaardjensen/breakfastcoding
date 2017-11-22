package com.example;

import com.example.service.ServiceExecutor;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

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
        String responseMsg = target.path("greetings").request().accept("application/json").get(String.class);
        assertEquals("{\"greeting\":\"Hallo!\"}", responseMsg);
    }

    @Test
    public void testGetNoFrenchGreeting() {
        String responseMsg = target.path("greetings").request().accept("application/json").acceptLanguage("fr").get(String.class);
        assertEquals("{\"greeting\":\"Hallo!\"}", responseMsg);
    }

    @Test
    public void testGetPreferredGreetingDanish() {
        String responseMsg = target.path("greetings").request().accept("application/json").acceptLanguage("da, en-gb;q=0.9, en;q=0.8, fr;q=0.5").get(String.class);
        assertEquals("{\"greeting\":\"Hallo!\"}", responseMsg);
    }

    @Test
    public void testGetPreferredGreetingEnglish() {
        String responseMsg = target.path("greetings").request().accept("application/json").acceptLanguage("en, da;q=0.9, en-gb;q=0.8, fr;q=0.5").get(String.class);
        assertEquals("{\"greeting\":\"Hello!\"}", responseMsg);
    }

    @Test
    public void testGetPreferredGreetingDanishEnglish() {
        String responseMsg = target.path("greetings").request().accept("application/json").acceptLanguage("da, en, da;q=0.9, en-gb;q=0.8, fr;q=0.5").get(String.class);
        assertEquals("{\"greeting\":\"Hallo!\"}", responseMsg);
    }

    @Test
    public void testGetPreferredGreetingEnglishDanish() {
        String responseMsg = target.path("greetings").request().accept("application/json").acceptLanguage("en, da, da;q=0.9, en-gb;q=0.8, fr;q=0.5").get(String.class);
        assertEquals("{\"greeting\":\"Hello!\"}", responseMsg);
    }

    @Test
    public void testGetEnglishGreeting() {
        String responseMsg = target.path("greetings").request().accept("application/json").acceptLanguage("en").get(String.class);
        assertEquals("{\"greeting\":\"Hello!\"}", responseMsg);
    }

    @Test
    public void testGetEnglishGreetingByBritish() {
        String responseMsg = target.path("greetings").request().accept("application/json").acceptLanguage("en-gb").get(String.class);
        assertEquals("{\"greeting\":\"Hello!\"}", responseMsg);
    }

    @Test
    public void testGetGreetingsList() {
        Response response = target.path("greetings").request().accept("application/hal+json").acceptLanguage("en").get(Response.class);
        String msg = response.readEntity(String.class);
        assertEquals("{"
                + "\"greetings\": {"
                + "\"info\": \"a list containing current greetings\","
                + "\"_links\": {"
                + "    \"self\": {"
                + "        \"href\":\"/greetings\","
                + "        \"type\": \"application/hal+json;concept=greeetinglist;v=1\","
                + "        \"title\": \"List of Greetings\""
                + "      },"
                + "    \"greetings\":"
                + "        [{"
                + "            \"href\": \"/greetings/hallo\","
                + "            \"title\": \"Danish Greeting - Hallo\""
                + "        }, {"
                + "             \"href\": \"/greetings/hello\","
                + "             \"title\": \"English Greeting - Hello\""
                + "        }]"
                + "        }"
                + "    }"
                + "}", msg);
    }
    
    @Test
    public void testHelloGreetingFromEnglishV2() {
        Response response = target.path("greetings/hello").request().accept("application/hal+json;concept=greeting").acceptLanguage("en").get(Response.class);
        String msg = response.readEntity(String.class);
        assertEquals("{"
                    + "  \"greeting\": \"Hello!\","
                    + "  \"language\": \"English\","
                    + "  \"country\": \"England\","
                    + "  \"native\": {"
                    + "    \"language\": \"English\","
                    + "    \"country\": \"England\""
                    + "  },"
                    + "  \"_links\": {"
                    + "    \"href\": \"/greetings/hello\","
                    + "    \"title\": \"English Greeting Hallo\""
                    + "  }"
                    + "}", msg);
    }
    @Test
    public void testHelloGreetingFromEnglishV2Specific() {
        Response response = target.path("greetings/hello").request().accept("application/hal+json;concept=greeting;v=2").acceptLanguage("en").get(Response.class);
        String msg = response.readEntity(String.class);
        assertEquals("{"
                    + "  \"greeting\": \"Hello!\","
                    + "  \"language\": \"English\","
                    + "  \"country\": \"England\","
                    + "  \"native\": {"
                    + "    \"language\": \"English\","
                    + "    \"country\": \"England\""
                    + "  },"
                    + "  \"_links\": {"
                    + "    \"href\": \"/greetings/hello\","
                    + "    \"title\": \"English Greeting Hallo\""
                    + "  }"
                    + "}", msg);
    }

    @Test
    public void testHelloGreetingFromEnglishV1Specific() {
        Response response = target.path("greetings/hello").request().accept("application/hal+json;concept=greeting;v=1").acceptLanguage("en").get(Response.class);
        String msg = response.readEntity(String.class);
        assertEquals("{"
                    + "  \"greeting\": \"Hello!\","
                    + "  \"country\": \"GB\","
                    + "  \"_links\": {"
                    + "    \"href\": \"/greetings/hello\","
                    + "    \"title\": \"English Greeting Hallo\""
                    + "  }"
                    + "}", msg);
    }

    @Test
    public void testHelloGreetingFromDane() {
        Response response = target.path("greetings/hello").request().accept("application/hal+json").acceptLanguage("da").get(Response.class);
        String msg = response.readEntity(String.class);
        assertEquals("{"
                    + "  \"greeting\": \"Hello!\","
                    + "  \"language\": \"English\","
                    + "  \"country\": \"England\","
                    + "  \"native\": {"
                    + "    \"language\": \"Engelsk\","
                    + "    \"country\": \"England\""
                    + "  },"
                    + "  \"_links\": {"
                    + "    \"href\": \"/greetings/hello\","
                    + "    \"title\": \"Engelsk Hilsen Hello\""
                    + "  }"
                    + "}", msg);
    }
    
    @Test
    public void testHalloGreetingFromDaneV1() {
        Response response = target.path("greetings/hallo").request().accept("application/hal+json;concept=greeting;v=1").acceptLanguage("da").get(Response.class);
        String msg = response.readEntity(String.class);
        assertEquals("{"
                    + "  \"greeting\": \"Hallo!\","
                    + "  \"country\": \"DK\","
                    + "  \"_links\": {"
                    + "    \"href\": \"/greetings/hallo\","
                    + "    \"title\": \"Dansk Hilsen Hallo\""
                    + "  }"
                    + "}", msg);
    }

    @Test
    public void testHalloGreetingFromEnglish() {
        Response response = target.path("greetings/hallo").request().accept("application/hal+json").acceptLanguage("en").get(Response.class);
        String msg = response.readEntity(String.class);
        assertEquals("{"
                + "  \"greeting\": \"Hallo!\","
                + "  \"language\": \"Dansk\","
                + "  \"country\": \"Danmark\","
                + "  \"native\": {"
                + "    \"language\": \"Danish\","
                + "    \"country\": \"Denmark\""
                + "  },"
                + "  \"_links\": {"
                + "    \"href\": \"/greetings/hallo\","
                + "    \"title\": \"Danish Greeting Hallo\""
                + "  }"
                + "}", msg);
    }
    
    @Test
    public void testNonExistentGreeting() {
        Response response = target.path("greetings/ballo").request().accept("application/hal+json").acceptLanguage("en").get(Response.class);
        String msg = response.readEntity(String.class);
        assertEquals("{"
                    + "  \"message\": \"Sorry your greeting does not exist yet!\","
                    + "  \"_links\":{"
                    + "      \"href\":\"/greetings\","
                    + "      \"type\":\"application/hal+json\","
                    + "      \"title\":\"List of exixting greetings\""
                    + "      }"
                    + "}", msg);
    }
}
