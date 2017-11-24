package com.example;

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
        assertEquals(entity, response.readEntity(String.class));
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
        entity = "{\"greeting\":\"Mooojn!\",\"language\":\"Synnejysk\",\"country\":\"Danmark\",\"native\":{\"language\":\"Dansk\",\"country\":\"Danmark\"},\"_links\":{\"self\":{\"href\":\"greetings/mooojn\",\"title\":\"Sønderjysk Hilsen Møøøjn\"}}}";
        assertEquals(entity, response.readEntity(String.class));
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
        assertEquals(entity, response.readEntity(String.class));
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
        assertEquals(entity, response.readEntity(String.class));
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

    @Test
    public void testUpdateNonParseablePatch() {
        final WebTarget target = target("greetings/hallo");
        Response         response = target
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
                        Entity.entity("{\"operation\":\"someotherop\",\"path\":\"language\",\"value\":\"itisnotgonnahappen\"}",
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
    }

    @Test
    public void testUpdateExistingOperationNonExistingAttribueGreetingLanguage() {
        final WebTarget target = target("greetings/moooooojn");
        String entity = "{\"greeting\":\"Mooojn!\",\"language\":\"Dansk\",\"country\":\"Danmark\",\"native\":{\"language\":\"Dansk\",\"country\":\"Danmark\"},\"_links\":{\"self\":{\"href\":\"greetings/moooooojn\",\"title\":\"Sønderjysk Hilsen Møøøjn\"}}}";
        Response response = target
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
                        Entity.entity("{\"op\":\"replace\",\"path\":\"links/self\",\"value\":\"notReplaced\"}",
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
}
