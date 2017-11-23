package com.example;

import java.util.Locale;
import java.util.UUID;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.example.service.ServiceExecutor;
import org.glassfish.grizzly.http.server.HttpServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
    public void testGetDynamicGreetingsList() {
        Response response = target.path("greetings").request().accept("application/hal+json").acceptLanguage("da").get(Response.class);
        String msg = response.readEntity(String.class);
        assertEquals("{\"greetings\":{\"info\":\"a list containing current greetings\",\"_links\":{\"self\":{\"href\":\"/greetings\",\"type\":\"application/hal+json;concept=greetinglist;v=1\",\"title\":\"List of Greetings\"},\"greetings\":[{\"href\":\"greetings/hallo\",\"title\":\"Dansk Hilsen Hallo\"},{\"href\":\"greetings/hallo\",\"title\":\"Danish Greeting Hallo\"},{\"href\":\"greetings/hello\",\"title\":\"English Greeting Hello\"},{\"href\":\"greetings/hello\",\"title\":\"Engelsk Hilsen Hello\"}]}}}", msg);
        assertEquals("application/hal+json;concept=greetings;v=1", response.getMediaType().toString());
                String entity = "{\"greeting\":\"Hej!\",\"language\":\"Dansk\",\"country\":\"Danmark\",\"native\":{\"language\":\"Dansk\",\"country\":\"Danmark\"},\"_links\":{\"self\":{\"href\":\"greetings/hej\",\"title\":\"Dansk Hilsen Hej\"}}}";
        response = target
                .path("greetings")
                .request()
                .accept("application/hal+json")
                .acceptLanguage("da")
                .post(Entity.json(entity));        
        assertEquals(201, response.getStatus());
        assertTrue(response.getHeaderString("Location").contains("greetings/hej"));
        response = target.path("greetings/hej").request().accept("application/hal+json").acceptLanguage("da").get(Response.class);
        assertEquals(200, response.getStatus());
        assertEquals(entity, response.readEntity(String.class));
        response = target.path("greetings").request().accept("application/hal+json").acceptLanguage("da").get(Response.class);
        String msgAfter = response.readEntity(String.class);
        assertTrue(msgAfter.length() > msg.length());
        assertFalse(msg.contains("Hej"));
        assertTrue(msgAfter.contains("Hej"));
    }

    @Test
    public void testHelloGreetingFromEnglishV2() {
        Response response = target
                .path("greetings/hello")
                .request()
                .accept("application/hal+json;concept=greeting;v=2")
                .acceptLanguage("en").get(Response.class);
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
        String contentType = response.getMediaType().toString();
        assertEquals("application/hal+json;concept=greeting;v=", contentType.substring(0, contentType.length() - 1));
        assertNotNull(response.getHeaders().get("X-Log-Token"));
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
        assertEquals("application/hal+json;concept=greeting;v=2", response.getMediaType().toString());
        assertNotNull(response.getHeaders().get("X-Log-Token"));
    }

    @Test
    public void testHelloGreetingFromEnglishV1Specific() {
        Response response = target
                .path("greetings/hello")
                .request()
                .accept("application/hal+json;concept=greeting;v=1")
                .acceptLanguage("en")
                .get(Response.class);
        String msg = response.readEntity(String.class);
        assertEquals("{"
                + "  \"greeting\": \"Hello!\","
                + "  \"country\": \"GB\","
                + "  \"_links\": {"
                + "    \"href\": \"/greetings/hello\","
                + "    \"title\": \"English Greeting Hallo\""
                + "  }"
                + "}", msg);
        assertEquals("application/hal+json;concept=greeting;v=1", response.getMediaType().toString());
        assertNotNull(response.getHeaders().get("X-Log-Token"));
    }

    @Test
    public void testHelloGreetingFromDanishV1Specific() {
        Response response = target
                .path("greetings/hello")
                .request().accept("application/hal+json;concept=greeting;v=1")
                .acceptLanguage("da")
                .get(Response.class);
        String msg = response.readEntity(String.class);
        assertEquals("{"
                + "  \"greeting\": \"Hello!\","
                + "  \"country\": \"GB\","
                + "  \"_links\": {"
                + "    \"href\": \"/greetings/hello\","
                + "    \"title\": \"Engelsk Hilsen Hello\""
                + "  }"
                + "}", msg);
        assertEquals("application/hal+json;concept=greeting;v=1", response.getMediaType().toString());
        assertNotNull(response.getHeaders().get("X-Log-Token"));
    }

    @Test
    public void testHelloGreetingFromDane() {
        Response response = target.path("greetings/hello").request().accept("application/hal+json").acceptLanguage("da").get(Response.class);
        String msg = response.readEntity(String.class);
        assertEquals("{"
                + "\"greeting\":\"Hello!\","
                + "\"language\":\"English\","
                + "\"country\":\"England\","
                + "\"native\":{"
                +  "\"language\":\"Engelsk\","
                +  "\"country\":\"England\""
                + "},"
                + "\"_links\":{"
                +  "\"self\":{"
                +   "\"href\":\"greetings/hello\","
                +   "\"title\":\"Engelsk Hilsen Hello\""
                +   "}"
                +  "}"
                + "}", msg);
        String contentType = response.getMediaType().toString();
        assertEquals("application/hal+json;concept=greeting;v=", contentType.substring(0, contentType.length() - 1));
        assertNotNull(response.getHeaders().get("X-Log-Token"));
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
        assertEquals("application/hal+json;concept=greeting;v=1", response.getMediaType().toString());
        assertNotNull(response.getHeaders().get("X-Log-Token"));
    }

    @Test
    public void testHalloGreetingFromEnglishV1() {
        Response response = target
                .path("greetings/hallo")
                .request().accept("application/hal+json;concept=greeting;v=1")
                .acceptLanguage("en")
                .get(Response.class);
        String msg = response.readEntity(String.class);
        assertEquals("{"
                + "  \"greeting\": \"Hallo!\","
                + "  \"country\": \"DK\","
                + "  \"_links\": {"
                + "    \"href\": \"/greetings/hallo\","
                + "    \"title\": \"Danish Greeting Hallo\""
                + "  }"
                + "}", msg);
        assertEquals("application/hal+json;concept=greeting;v=1", response.getMediaType().toString());
        assertNotNull(response.getHeaders().get("X-Log-Token"));
    }

    @Test
    public void testAlloGreetingFromDaneV1() {
        String uuid = UUID.randomUUID().toString();
        Response response = target
                .path("greetings/allo").request()
                .accept("application/hal+json;concept=greeting;v=1")
                .acceptLanguage("da")
                .header("X-Log-Token", uuid)
                .get(Response.class);
        String msg = response.readEntity(String.class);
        assertEquals("{"
                + "  \"message\": \"Sorry your greeting does not exist yet!\","
                + "  \"_links\":{"
                + "      \"href\":\"/greetings\","
                + "      \"type\":\"application/hal+json\","
                + "      \"title\":\"List of exixting greetings\""
                + "      }"
                + "}", msg);
        assertEquals("application/hal+json", response.getMediaType().toString());
        assertNotNull(response.getHeaders().get("X-Log-Token"));
        assertEquals(uuid, response.getHeaders().get("X-Log-Token").get(0));
    }

    @Test
    public void testHalloGreetingFromEnglish() {
        Response response = target.path("greetings/hallo").request().accept("application/hal+json").acceptLanguage("en").get(Response.class);
        String msg = response.readEntity(String.class);
        assertEquals("{"
                + "\"greeting\":\"Hallo!\","
                + "\"language\":\"Dansk\","
                + "\"country\":\"Danmark\","
                + "\"native\":{"
                +  "\"language\":\"Danish\","
                +  "\"country\":\"Denmark\""
                + "},"
                + "\"_links\":{"
                +   "\"self\":{"
                +    "\"href\":\"greetings/hallo\","
                +    "\"title\":\"Danish Greeting Hallo\""
                +   "}"
                +  "}"
                + "}", msg);
        String contentType = response.getMediaType().toString();
        assertEquals("application/hal+json;concept=greeting;v=", contentType.substring(0, contentType.length() - 1));
        assertNotNull(response.getHeaders().get("X-Log-Token"));
    }

    @Test
    public void testHalloGreetingFromDanish() {
        Response response = target.path("greetings/hallo").request().accept("application/hal+json").acceptLanguage("da").get(Response.class);
        String msg = response.readEntity(String.class);
        assertEquals("{"
                + "\"greeting\":\"Hallo!\","
                + "\"language\":\"Dansk\","
                + "\"country\":\"Danmark\","
                + "\"native\":{"
                +  "\"language\":\"Dansk\","
                +  "\"country\":\"Danmark\""
                + "},"
                + "\"_links\":{"
                +  "\"self\":{"
                +   "\"href\":\"greetings/hallo\","
                +   "\"title\":\"Dansk Hilsen Hallo\""
                +   "}"
                +  "}"
                + "}", msg);
        String contentType = response.getMediaType().toString();
        assertEquals("application/hal+json;concept=greeting;v=", contentType.substring(0, contentType.length() - 1));
        assertNotNull(response.getHeaders().get("X-Log-Token"));
    }

    @Test
    public void testHalloGreetingFromEmptyDanish() {
        Response response = target.path("greetings/hallo").request().accept("application/hal+json").acceptLanguage("").get(Response.class);
        String msg = response.readEntity(String.class);
        assertEquals("{"
                + "\"greeting\":\"Hallo!\","
                + "\"language\":\"Dansk\","
                + "\"country\":\"Danmark\","
                + "\"native\":{"
                +  "\"language\":\"Dansk\","
                +  "\"country\":\"Danmark\""
                + "},"
                + "\"_links\":{"
                +  "\"self\":{"
                +   "\"href\":\"greetings/hallo\","
                +   "\"title\":\"Dansk Hilsen Hallo\""
                +  "}"
                + "}"
                + "}", msg);
        String contentType = response.getMediaType().toString();
        assertEquals("application/hal+json;concept=greeting;v=", contentType.substring(0, contentType.length() - 1));
        assertNotNull(response.getHeaders().get("X-Log-Token"));
    }

    @Test
    public void testHalloGreetingFromNullDanish() {
        Locale locale = null;
        Response response = target.path("greetings/hallo").request().accept("application/hal+json").acceptLanguage(locale).get(Response.class);
        String msg = response.readEntity(String.class);
        assertEquals("{"
                + "\"greeting\":\"Hallo!\","
                + "\"language\":\"Dansk\","
                + "\"country\":\"Danmark\","
                + "\"native\":{"
                +  "\"language\":\"Dansk\","
                +  "\"country\":\"Danmark\""
                + "},"
                + "\"_links\":{"
                +  "\"self\":{"
                +   "\"href\":\"greetings/hallo\","
                +   "\"title\":\"Dansk Hilsen Hallo\""
                +   "}"
                +  "}"
                + "}", msg);
        String contentType = response.getMediaType().toString();
        assertEquals("application/hal+json;concept=greeting;v=", contentType.substring(0, contentType.length() - 1));
        assertNotNull(response.getHeaders().get("X-Log-Token"));
    }

    @Test
    public void testHalloGreetingFromDanishWithLogToken() {
        Response response = target.path("greetings/hallo")
                .request()
                .accept("application/hal+json")
                .acceptLanguage("da")
                .header("X-Log-Token", "noget-man-kan-kende")
                .get(Response.class);
        assertNotNull(response.getHeaders().get("X-Log-Token"));
        assertEquals("noget-man-kan-kende", response.getHeaderString("X-Log-Token"));
    }

    @Test
    public void testHalloGreetingFromDanishWithEmptyLogToken() {
        Response response = target.path("greetings/hallo")
                .request()
                .accept("application/hal+json")
                .acceptLanguage("da")
                .header("X-Log-Token", "")
                .get(Response.class);
        assertNotNull(response.getHeaders().get("X-Log-Token"));
    }

    @Test
    public void testHalloGreetingFromDanishWithNullLogToken() {
        Response response = target.path("greetings/hallo")
                .request()
                .accept("application/hal+json")
                .acceptLanguage("da")
                .header("X-Log-Token", null)
                .get(Response.class);
        assertNotNull(response.getHeaders().get("X-Log-Token"));
    }

    @Test
    public void testHalloGreetingFromDanishWithoutLogToken() {
        Response response = target.path("greetings/hallo")
                .request()
                .accept("application/hal+json")
                .acceptLanguage("da")
                .get(Response.class);
        assertNotNull(response.getHeaders().get("X-Log-Token"));
    }

    @Test
    public void testHalloGreetingFromDanishWithNonChangedETagAndLastModified() {
        Response response = target.path("greetings/hallo")
                .request()
                .accept("application/hal+json")
                .acceptLanguage("da")
                .get(Response.class);
        assertNotNull(response.getHeaders().get("etag"));
        String etag = response.getHeaderString("etag");
        assertNotNull(response.getHeaders().get("last-modified"));
        String lastModified = response.getHeaderString("last-modified");
        assertEquals(200, response.getStatus());
        response = target.path("greetings/hallo")
                .request()
                .accept("application/hal+json")
                .acceptLanguage("da")
                .header("If-None-Match", etag)
                .header("If-Modified-Since", lastModified)
                .get(Response.class);
        assertEquals(304, response.getStatus());
    }

    @Test
    public void testHalloGreetingFromDanishWithChangedETagAndNonChangedLastModified() {
        Response response = target.path("greetings/hallo")
                .request()
                .accept("application/hal+json")
                .acceptLanguage("da")
                .get(Response.class);
        assertNotNull(response.getHeaders().get("etag"));
        String etag = response.getHeaderString("etag");
        String lastModified = response.getHeaderString("last-modified");
        assertEquals(200, response.getStatus());
        response = target.path("greetings/hallo")
                .request()
                .accept("application/hal+json")
                .acceptLanguage("da")
                .header("If-None-Match", etag)
                .header("If-Modified-Since", lastModified.substring(0, lastModified.length() - 1))
                .get(Response.class);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testHalloGreetingFromDanishWithChangedETagAndNotModifiedLastModified() {
        Response response = target.path("greetings/hallo")
                .request()
                .accept("application/hal+json")
                .acceptLanguage("da")
                .get(Response.class);
        assertNotNull(response.getHeaders().get("etag"));
        String etag = response.getHeaderString("etag");
        assertNotNull(response.getHeaders().get("last-modified"));
        String lastModified = response.getHeaderString("last-modified");
        assertEquals(200, response.getStatus());
        response = target.path("greetings/hallo")
                .request()
                .accept("application/hal+json")
                .acceptLanguage("da")
                .header("If-None-Match", "\"nomatch\"")
                .header("If-Modified-Since", lastModified)
                .get(Response.class);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testNonExistentGreeting() {
        Response response = target.path("greetings/ballo").request().accept("application/hal+json").acceptLanguage("en").get(Response.class);
        String msg = response.readEntity(String.class);
        assertEquals(
                "{"
                +  "\"message\":\"Sorry your greeting does not exist yet!\","
                +  "\"_links\":{"
                +  "\"greetings\":{"
                +   "\"href\":\"/greetings\","
                +    "\"type\":\"application/hal+json\","
                +    "\"title\":\"List of exixting greetings\""
                +   "}"
                +  "}"
                + "}", msg);
        assertEquals("application/hal+json", response.getMediaType().toString());
        assertNotNull(response.getHeaders().get("X-Log-Token"));
    }

    @Test
    public void testNonExistentGreetingG1V2() {
        Response response = target.path("greetings/ballo").request().accept("application/hal+json;concept=greeting;v=2").acceptLanguage("en").get(Response.class);
        String msg = response.readEntity(String.class);
        assertEquals("{"
                    + "  \"message\": \"Sorry your greeting does not exist yet!\","
                    + "  \"_links\":{"
                    + "      \"href\":\"/greetings\","
                    + "      \"type\":\"application/hal+json\","
                    + "      \"title\":\"List of exixting greetings\""
                    + "      }"
                    + "}", msg);
        assertEquals("application/hal+json", response.getMediaType().toString());
        assertNotNull(response.getHeaders().get("X-Log-Token"));
    }

    @Test
    public void testNonSupportedAccept() {
        Response response = target.path("greetings/hallo").request().accept("application/hal+json;concept=unrealgreeting").acceptLanguage("en").get(Response.class);
        assertEquals(415, response.getStatus());
    }
    
    @Test
    public void testCreateFrenchGreetingForDanish() {
        String entity = "{\"greeting\":\"Allo!\",\"language\":\"Fransk\",\"country\":\"Frankrig\",\"native\":{\"language\":\"Français\",\"country\":\"France\"},\"_links\":{\"self\":{\"href\":\"greetings/allo\",\"title\":\"Fransk Hilsen Allo\"}}}";
        Response response = target
                .path("greetings")
                .request()
                .accept("application/hal+json")
                .acceptLanguage("da")
                .post(Entity.json(entity));        
        assertEquals(201, response.getStatus());
        assertTrue(response.getHeaderString("Location").contains("greetings/allo"));
        response = target.path("greetings/allo").request().accept("application/hal+json").acceptLanguage("da").get(Response.class);
        assertEquals(200, response.getStatus());
        assertEquals(entity, response.readEntity(String.class));
    }

    @Test
    public void testReCreateFrenchGreetingForDanish() {
        String entity = "{\"greeting\":\"Allo!\",\"language\":\"Fransk\",\"country\":\"Frankrig\",\"native\":{\"language\":\"Français\",\"country\":\"France\"},\"_links\":{\"self\":{\"href\":\"greetings/allo\",\"title\":\"Fransk Hilsen Allo Rettet\"}}}";
        Response response = target
                .path("greetings")
                .request()
                .accept("application/hal+json")
                .acceptLanguage("da")
                .post(Entity.json(entity));        
        assertEquals(201, response.getStatus());
        assertTrue(response.getHeaderString("Location").contains("greetings/allo"));
        response = target.path("greetings/allo").request().accept("application/hal+json").acceptLanguage("da").get(Response.class);
        assertEquals(200, response.getStatus());
        assertEquals(entity, response.readEntity(String.class));
    }

    @Test
    public void testCreateFrenchGreetingForEnglish() {
        String entity = "{\"greeting\":\"Allo!\",\"language\":\"French\",\"country\":\"France\",\"native\":{\"language\":\"Français\",\"country\":\"France\"},\"_links\":{\"self\":{\"href\":\"greetings/allo\",\"title\":\"French Greeting Allo\"}}}";
        Response response = target
                .path("greetings")
                .request()
                .accept("application/hal+json")
                .acceptLanguage("en")
                .post(Entity.json(entity));        
        assertEquals(201, response.getStatus());
        assertTrue(response.getHeaderString("Location").contains("greetings/allo"));
        response = target.path("greetings/allo").request().accept("application/hal+json").acceptLanguage("en").get(Response.class);
        assertEquals(200, response.getStatus());
        assertEquals(entity, response.readEntity(String.class));
    }

    @Test
    public void testCreateFrenchGreetingForFrench() {
        String entity = "{\"greeting\":\"Allo!\",\"language\":\"Français\",\"country\":\"France\",\"native\":{\"language\":\"Français\",\"country\":\"France\"},\"_links\":{\"self\":{\"href\":\"greetings/allo\",\"title\":\"Un Salute Français Allo\"}}}";
        Response response = target
                .path("greetings")
                .request()
                .accept("application/hal+json")
                .acceptLanguage("fr")
                .post(Entity.json(entity));        
        assertEquals(201, response.getStatus());
        assertTrue(response.getHeaderString("Location").contains("greetings/allo"));
        response = target.path("greetings/allo").request().accept("application/hal+json").acceptLanguage("fr").get(Response.class);
        assertEquals(200, response.getStatus());
        assertEquals(entity, response.readEntity(String.class));
    }

    @Test
    public void testCreateFrenchGreetingForGerman() {
        String entity = "{\"greeting\":\"Allo!\",\"language\":\"Französisch\",\"country\":\"Frankreich\",\"native\":{\"language\":\"Français\",\"country\":\"France\"},\"_links\":{\"self\":{\"href\":\"greetings/allo\",\"title\":\"Ein Französischer Grüß\"}}}";
        Response response = target
                .path("greetings")
                .request()
                .accept("application/hal+json")
                .acceptLanguage("de")
                .post(Entity.json(entity));        
        assertEquals(201, response.getStatus());
        assertTrue(response.getHeaderString("Location").contains("greetings/allo"));
        response = target.path("greetings/allo").request().accept("application/hal+json").acceptLanguage("de").get(Response.class);
        assertEquals(200, response.getStatus());
        assertEquals(entity, response.readEntity(String.class));
    }
    
    @Test
    public void testUnparsableInput() {
        String entity = "{\"noField\":\"Allo!\",\"language\":\"Französisch\",\"country\":\"Frankreich\",\"native\":{\"language\":\"Français\",\"country\":\"France\"},\"_links\":{\"self\":{\"href\":\"greetings/allo\",\"title\":\"Ein Französischer Grüß\"}}}";
        Response response = target
                .path("greetings")
                .request()
                .accept("application/hal+json")
                .acceptLanguage("da")
                .post(Entity.json(entity));        
        assertEquals(400, response.getStatus());
    }
}
