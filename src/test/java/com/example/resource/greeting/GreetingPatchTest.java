package com.example.resource.greeting;

import java.io.IOException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Response;

import com.example.service.ServiceExecutor;
import com.example.service.patch.JSONPatchContainer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.grizzly.connector.GrizzlyConnectorProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GreetingPatchTest extends JerseyTest {

    @Override
    protected ResourceConfig configure() {
        return ServiceExecutor.create();
    }

    @Override
    protected void configureClient(ClientConfig config) {
        config.register(ServiceExecutor.createMoxyJsonResolver())
                .connectorProvider(new GrizzlyConnectorProvider());
    }

    @Test
    public void testUpdateGreetingLanguage() {
        final WebTarget target = target("greetings/mooojn");
        String entity = "{\"greeting\":\"Mooojn!\",\"language\":\"Dansk\",\"country\":\"Danmark\",\"native\":{\"language\":\"Dansk\",\"country\":\"Danmark\"},\"_links\":{\"self\":{\"href\":\"greetings/mooojn\",\"title\":\"Sønderjysk Hilsen Møøøjn\"}}}";
        Response response = target
                .request()
                .acceptLanguage("son")
                .method("PUT", Entity.entity(entity, "application/json"), Response.class);
        assertEquals(201, response.getStatus());
        assertTrue(response.getHeaderString("Location").contains("greetings/mooojn"));
        response = target
                .request()
                .accept("application/json")
                .acceptLanguage("son")
                .get(Response.class);
        assertEquals(200, response.getStatus());
        String msg = response.readEntity(String.class);
        assertTrue(msg.contains("\"greeting\":\"Mooojn!\","));
        assertTrue(msg.contains("\"language\":\"Dansk\","));
        assertTrue(msg.contains("\"country\":\"Danmark\","));
        assertTrue(msg.contains("\"native\":{\"language\":\"Dansk\",\"country\":\"Danmark\"}"));
        assertTrue(msg.contains("\"_links\":{\"self\":{\"href\":"));
        assertTrue(msg.contains("greetings/mooojn\",\"title\":\"Sønderjysk Hilsen Møøøjn\"}}"));

        EntityTag eTag = response.getEntityTag();
        response = target
                .request()
                .acceptLanguage("son")
                .method("PATCH",
                        Entity.entity("{\"op\":\"replace\",\"path\":\"language\",\"value\":\"Synnejysk\"}",
                                "application/patch+json"),
                        Response.class);
        assertEquals(409, response.getStatus());
        response = target
                .request()
                .acceptLanguage("son")
                .header("If-None-Match", eTag)
                .method("PATCH",
                        Entity.entity("{\"op\":\"replace\",\"path\":\"language\",\"value\":\"Synnejysk\"}",
                                "application/patch+json"),
                        Response.class);
        assertEquals(200, response.getStatus());
        assertTrue(response.readEntity(String.class).contains("value is replaced"));
        response = target
                .request()
                .accept("application/json")
                .acceptLanguage("son")
                .get(Response.class);
        assertEquals(200, response.getStatus());
        msg = response.readEntity(String.class);
        assertTrue(msg.contains("\"greeting\":\"Mooojn!\","));
        assertTrue(msg.contains("\"language\":\"Synnejysk\","));
        assertTrue(msg.contains("\"country\":\"Danmark\","));
        assertTrue(msg.contains("\"native\":{\"language\":\"Dansk\",\"country\":\"Danmark\"}"));
        assertTrue(msg.contains("\"_links\":{\"self\":{\"href\":"));
        assertTrue(msg.contains("greetings/mooojn\",\"title\":\"Sønderjysk Hilsen Møøøjn\"}}"));
    }

    @Test
    public void testUpdateGreetingLanguageWrongContentType() {
        final WebTarget target = target("greetings/moooojn");
        String entity = "{\"greeting\":\"Mooojn!\",\"language\":\"Dansk\",\"country\":\"Danmark\",\"native\":{\"language\":\"Dansk\",\"country\":\"Danmark\"},\"_links\":{\"self\":{\"href\":\"greetings/moooojn\",\"title\":\"Sønderjysk Hilsen Møøøjn\"}}}";
        Response response = target
                .request()
                .acceptLanguage("son")
                .method("PUT", Entity.entity(entity, "application/json"), Response.class);
        assertEquals(201, response.getStatus());
        assertTrue(response.getHeaderString("Location").contains("greetings/moooojn"));
        response = target
                .request()
                .accept("application/json")
                .acceptLanguage("son")
                .get(Response.class);
        assertEquals(200, response.getStatus());
        String msg = response.readEntity(String.class);
        assertTrue(msg.contains("\"greeting\":\"Mooojn!\","));
        assertTrue(msg.contains("\"language\":\"Dansk\","));
        assertTrue(msg.contains("\"country\":\"Danmark\","));
        assertTrue(msg.contains("\"native\":{\"language\":\"Dansk\",\"country\":\"Danmark\"}"));
        assertTrue(msg.contains("\"_links\":{\"self\":{\"href\":"));
        assertTrue(msg.contains("greetings/moooojn\",\"title\":\"Sønderjysk Hilsen Møøøjn\"}}"));
        EntityTag eTag = response.getEntityTag();
        response = target
                .request()
                .acceptLanguage("son")
                .header("If-None-Match", eTag)
                .method("PATCH",
                        Entity.entity("{\"op\":\"replace\",\"path\":\"language\",\"value\":\"Synnejysk\"}",
                                "application/some+json"),
                        Response.class);
        assertEquals(415, response.getStatus());
        response = target
                .request()
                .accept("application/json")
                .acceptLanguage("son")
                .get(Response.class);
        assertEquals(200, response.getStatus());
        msg = response.readEntity(String.class);
        assertFalse(msg.contains("\"language\":\"Synnejysk\","));
    }

    @Test
    public void testUpdateNonParseablePath() {
        final WebTarget target = target("greetings/hallo");
        Response response = target
                .request()
                .accept("application/json")
                .acceptLanguage("en")
                .get(Response.class);
        assertEquals(200, response.getStatus());
        EntityTag eTag = response.getEntityTag();
        response = target
                .request()
                .acceptLanguage("en")
                .header("If-None-Match", eTag)
                .method("PATCH",
                        Entity.entity("{\"op\":\"replace\",\"path\":\"language/nonexisting\",\"value\":\"itisnotgonnahappen\"}",
                                "application/patch+json"),
                        Response.class);
        assertEquals(400, response.getStatus());      
    }

    @Test
    public void testUpdateNonParseablePatch() {
        WebTarget target = target("greetings/hallo");
        Response response = target
                .request()
                .accept("application/json")
                .acceptLanguage("en")
                .get(Response.class);
        assertEquals(200, response.getStatus());
        EntityTag eTag = response.getEntityTag();
        response = target
                .request()
                .acceptLanguage("en")
                .header("If-None-Match", eTag)
                .method("PATCH",
                        Entity.entity("{\"operation\":\"replace\",\"path\":\"language\",\"value\":\"itisnotgonnahappen\"}",
                                "application/patch+json"),
                        Response.class);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void testUpdateNonExistingOperationGreetingLanguage() {
        final WebTarget target = target("greetings/mooooojn");
        String entity = "{\"greeting\":\"Mooojn!\",\"language\":\"Dansk\",\"country\":\"Danmark\",\"native\":{\"language\":\"Dansk\",\"country\":\"Danmark\"},\"_links\":{\"self\":{\"href\":\"greetings/mooooojn\",\"title\":\"Sønderjysk Hilsen Møøøjn\"}}}";
        Response response = target
                .request()
                .accept("application/json")
                .acceptLanguage("son")
                .get(Response.class);
        assertEquals(404, response.getStatus());
        response = target
                .request()
                .acceptLanguage("son")
                .method("PUT", Entity.entity(entity, "application/json"), Response.class);
        assertEquals(201, response.getStatus());
        assertTrue(response.getHeaderString("Location").contains("greetings/mooooojn"));
        response = target
                .request()
                .accept("application/json")
                .acceptLanguage("son")
                .get(Response.class);
        assertEquals(200, response.getStatus());
        EntityTag eTag = response.getEntityTag();
        response = target
                .request()
                .acceptLanguage("son")
                .header("If-None-Match", eTag)
                .method("PATCH",
                        Entity.entity("{\"op\":\"someotherop\",\"path\":\"language\",\"value\":\"itisnotgonnahappen\"}",
                                "application/patch+json"),
                        Response.class);
        assertEquals(400, response.getStatus());
        assertTrue(response.readEntity(String.class).contains("{\"error\":\"only operation replace is supported\"}"));
    }

    @Test
    public void testUpdateExistingOperationNonExistingAttribueGreetingLanguage() {
        final WebTarget target = target("greetings/moooooojn");
        Response response = target
                .request()
                .accept("application/json")
                .acceptLanguage("son")
                .get(Response.class);
        assertEquals(404, response.getStatus());
        String entity = "{\"greeting\":\"Mooojn!\",\"language\":\"Dansk\",\"country\":\"Danmark\",\"native\":{\"language\":\"Dansk\",\"country\":\"Danmark\"},\"_links\":{\"self\":{\"href\":\"greetings/moooooojn\",\"title\":\"Sønderjysk Hilsen Møøøjn\"}}}";
        response = target
                .request()
                .acceptLanguage("son")
                .method("PUT", Entity.entity(entity, "application/json"), Response.class);
        assertEquals(201, response.getStatus());
        assertTrue(response.getHeaderString("Location").contains("greetings/moooooojn"));
        response = target
                .request()
                .accept("application/json")
                .acceptLanguage("son")
                .get(Response.class);
        assertEquals(200, response.getStatus());
        EntityTag eTag = response.getEntityTag();
        response = target
                .request()
                .acceptLanguage("son")
                .header("If-None-Match", eTag)
                .method("PATCH",
                        Entity.entity("{\"op\":\"replace\",\"path\":\"nonexisting\",\"value\":\"itisnotgonnahappen\"}",
                                "application/patch+json"),
                        Response.class);
        assertEquals(400, response.getStatus());
        String msg = response.readEntity(String.class);
        assertTrue(msg.contains("value was not replaced"));
        response = target
                .request()
                .acceptLanguage("son")
                .header("If-None-Match", eTag)
                .method("PATCH",
                        Entity.entity("{\"op\":\"replace\",\"path\":\"_links/self\",\"value\":\"notReplaced\"}",
                                "application/patch+json"),
                        Response.class);
        assertEquals(400, response.getStatus());
        msg = response.readEntity(String.class);
        assertTrue(msg.contains("value could not be replaced"));
    }

    @Test
    public void testMapper() throws IOException {
        String patch = "{\"op\":\"replace\",\"path\":\"language\",\"value\":\"Synnejysk\"}";
        ObjectMapper mapper = new ObjectMapper();
        JSONPatchContainer patchR = mapper.readValue(patch, JSONPatchContainer.class);
        assertEquals(patch, patchR.toString());
    }

    @Test
    public void testUpdateNonExistingGreetingLanguage() {
        final WebTarget target = target("greetings/itdoesnotexist");
        Response response = target
                .request()
                .acceptLanguage("en")
                .method("PATCH",
                        Entity.entity("{\"op\":\"replace\",\"path\":\"language\",\"value\":\"whoCares\"}",
                                "application/patch+json"),
                        Response.class);
        assertEquals(404, response.getStatus());
    }

}

