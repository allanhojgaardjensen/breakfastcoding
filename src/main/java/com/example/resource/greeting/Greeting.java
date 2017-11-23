package com.example.resource.greeting;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.constraints.Pattern;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Greetings Resource handles greetings in Danish and English.
 */
@Path("greetings")
@Api(value = "/greetings", tags = {"greetings"})
public class Greeting {

    private static final Logger LOGGER = Logger.getLogger(Greeting.class.getName());
    private final Map<String, GreetingProducer> greetingProducers = new HashMap<>();

    public Greeting() {
        greetingProducers.put("application/hal+json", this::getGreetingG1V2);
        greetingProducers.put("application/hal+json;concept=greeting", this::getGreetingG1V2);
        greetingProducers.put("application/hal+json;concept=greeting;v=1", this::getGreetingG1V1);
        greetingProducers.put("application/hal+json;concept=greeting;v=2", this::getGreetingG1V2);
    }

    /**
     * A Greeting can be addressed specifically and the consumer can specify what language he/she prefers.
     * <p>
     * A LogToken can be part of the request and that will be returned in the response. If no LogToken is present in the request a new one is
     * extracted and returned to the consumer. The format for the LogToken is a 36 long string that can consist of a-z, A-Z,0-9 and - In other
     * words: small letters, capital letters and numbers and hyphens
     * <p>
     * @param acceptLanguage the preferred language
     * @param accept the accepted response format
     * @param logToken a correlation id for a consumer
     * @return String that will be returned containing "application/hal+json".
     */
    @GET
    @Produces({"application/hal+json", "application/json"})
    @ApiOperation(value = "list all greetings"
            + " (the use of application/json is deprecated and will give the old greetings response)", response = String.class)
    public Response getGreetingsList(@HeaderParam("Accept-Language")
            @Pattern(regexp = "^((\\s*[a-z]{2},{0,1}(-{0,1}[a-z]{2}){0,1})+(;q=0\\.[1-9]){0,1},{0,1})+") String acceptLanguage,
            @HeaderParam("X-Log-Token") @Pattern(regexp = "^[a-zA-Z0-9\\-]{36}$") String logToken,
            @HeaderParam("Accept") String accept) {
        if ("application/json".equalsIgnoreCase(accept)) {
            return handleBackwardsCompliance(acceptLanguage, "{\"greeting\":\"Hallo!\"}");
        }

        final String responseEntity = "{"
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
                + "}";

        return Response.ok()
                .entity(responseEntity)
                .type("application/hal+json;concept=greetings;v=1")
                .header("X-Log-Token", validateOrCreateToken(logToken))
                .build();
    }

    /**
     * A Greeting can be addressed specifically and the consumer can specify what language he/she prefers.
     * <p>
     * A LogToken can be part of the request and that will be returned in the response. If no LogToken is present in the request a new one is
     * extracted and returned to the consumer. The format for the LogToken is a 36 long string that can consist of a-z, A-Z,0-9 and - In other
     * words: small letters, capital letters and numbers and hyphens
     * <p>
     * @param accept the chosen accepted content-type by consumer
     * @param acceptLanguage client can set the preferred preferredLanguage(s) as in HTTP spec.
     * @param logToken a correlation id for a consumer
     * @param greeting the greeting wanted by consumer
     * @return String that will be returned containing "application/hal+json".
     */
    @GET
    @Path("{greeting}")
    @Produces({"application/hal+json"})
    @ApiOperation(value = "get a greeting back with a preferred language portion included", response = String.class)
    public Response getGreeting(
            @Context Request request,
            @HeaderParam("Accept") String accept,
            @HeaderParam("Accept-Language") @Pattern(regexp = "^((\\s*[a-z]{2},{0,1}(-{0,1}[a-z]{2}){0,1})+(;q=0\\.[1-9]){0,1},{0,1})+") String acceptLanguage,
            @HeaderParam("X-Log-Token") @Pattern(regexp = "^[a-zA-Z0-9\\-]{36}$") String logToken,
            @PathParam("greeting") @Pattern(regexp = "[a-z]*") String greeting) {
        return greetingProducers.getOrDefault(accept, this::handle415Unsupported).getResponse(request, accept, acceptLanguage, greeting, logToken);
    }

    /**
     * Implements version one of the greeting service, where detailed information needs to be handled and returned to consumer, this
     * construction using interface and explicitly mapping content-types to methods allows to maintain multiple content versions in same service
     * endpoint and thus be able to ensure that consumers can roll back to this version once the next edition that is no longer compliant is
     * available.
     * <p>
     * The consumer roll back by entering the full content-type in the Accept header in this case {@code application/json;concept=greeting;v=1}
     * or more specific and correct as that is the actual format used. {@code application/hal+json;concept=greeting;v=1}
     */
    private Response getGreetingG1V2(Request request, String accept, String acceptLanguage, String greeting, String logToken) {
        String language = preferredLanguage(acceptLanguage);
        final String entity;
        switch (greeting) {
            case "hallo":
                entity = getDanishFull(language);
                return getResponse(request, logToken, entity);
            case "hello":
                entity = getEnglishFull(language);
                return getResponse(request, logToken, entity);
            default:
                entity = "{"
                        + "  \"message\": \"Sorry your greeting does not exist yet!\","
                        + "  \"_links\":{"
                        + "      \"href\":\"/greetings\","
                        + "      \"type\":\"application/hal+json\","
                        + "      \"title\":\"List of exixting greetings\""
                        + "      }"
                        + "}";
                return Response
                        .status(404)
                        .entity(entity)
                        .type("application/hal+json")
                        .header("X-Log-Token", validateOrCreateToken(logToken))
                        .build();
        }
    }

    private Response getResponse(Request request, String logToken, String entity) {
        Date lastModified = getLastModified();
        EntityTag eTag = new EntityTag(Integer.toHexString(entity.hashCode()), false);
        ResponseBuilder builder = request.evaluatePreconditions(lastModified, eTag);
        if (builder != null) {
            return builder.build();
        }
        return Response
                .ok(entity)
                .type("application/hal+json;concept=greeting;v=2")
                .tag(eTag)
                .lastModified(lastModified)
                .header("X-Log-Token", validateOrCreateToken(logToken))
                .build();
    }

    /**
     * Implements version one of the greeting service, where detailed information needs to be handled and returned to consumer, this
     * construction using interface and explicitly mapping content-types to methods allows to maintain multiple content versions in same service
     * endpoint and thus be able to ensure that consumers can roll back to this version once the next edition that is no longer compliant is
     * available.
     * <p>
     * The consumer roll back by entering the full content-type in the Accept header in this case {@code application/json;concept=greeting;v=1}
     * or more specific and correct as that is the actual format used. {@code application/hal+json;concept=greeting;v=1}
     */
    private Response getGreetingG1V1(Request request, String accept, String acceptLanguage, String greeting, String logToken) {
        String language = preferredLanguage(acceptLanguage);
        switch (greeting) {
            case "hallo":
                return Response
                        .ok(getDanish(language))
                        .type("application/hal+json;concept=greeting;v=1")
                        .header("X-Log-Token", validateOrCreateToken(logToken))
                        .build();
            case "hello":
                return Response
                        .status(200)
                        .entity(getEnglish(language))
                        .type("application/hal+json;concept=greeting;v=1")
                        .header("X-Log-Token", validateOrCreateToken(logToken))
                        .build();
            default:
                return Response
                        .status(404)
                        .entity("{"
                                + "  \"message\": \"Sorry your greeting does not exist yet!\","
                                + "  \"_links\":{"
                                + "      \"href\":\"/greetings\","
                                + "      \"type\":\"application/hal+json\","
                                + "      \"title\":\"List of exixting greetings\""
                                + "      }"
                                + "}")
                        .type("application/hal+json")
                        .header("X-Log-Token", validateOrCreateToken(logToken))
                        .build();
        }
    }

    private String preferredLanguage(String preferred) {
        if (preferred == null || preferred.isEmpty()) {
            return "da";
        }
        String[] languages = preferred.split(",");
        String[] preferredLanguage = Arrays.stream(languages).filter(s -> !s.contains(";")).toArray(String[]::new);
        return preferredLanguage[0];
    }

    private String getDanishFull(String language) {
        if (language.contains("en")) {
            return "{"
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
                    + "}";
        } else {
            return "{"
                    + "  \"greeting\": \"Hallo!\","
                    + "  \"language\": \"Dansk\","
                    + "  \"country\": \"Danmark\","
                    + "  \"native\": {"
                    + "    \"language\": \"Dansk\","
                    + "    \"country\": \"Danmark\""
                    + "  },"
                    + "  \"_links\": {"
                    + "    \"href\": \"/greetings/hallo\","
                    + "    \"title\": \"Dansk Hilsen Hallo\""
                    + "  }"
                    + "}";
        }
    }

    private String getEnglishFull(String language) {
        if (language.contains("da")) {
            return "{"
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
                    + "}";
        } else {
            return "{"
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
                    + "}";
        }
    }

    private String getDanish(String language) {
        if (language.contains("en")) {
            return "{"
                    + "  \"greeting\": \"Hallo!\","
                    + "  \"country\": \"DK\","
                    + "  \"_links\": {"
                    + "    \"href\": \"/greetings/hallo\","
                    + "    \"title\": \"Danish Greeting Hallo\""
                    + "  }"
                    + "}";
        } else {
            return "{"
                    + "  \"greeting\": \"Hallo!\","
                    + "  \"country\": \"DK\","
                    + "  \"_links\": {"
                    + "    \"href\": \"/greetings/hallo\","
                    + "    \"title\": \"Dansk Hilsen Hallo\""
                    + "  }"
                    + "}";
        }
    }

    private String getEnglish(String language) {
        if (language.contains("da")) {
            return "{"
                    + "  \"greeting\": \"Hello!\","
                    + "  \"country\": \"GB\","
                    + "  \"_links\": {"
                    + "    \"href\": \"/greetings/hello\","
                    + "    \"title\": \"Engelsk Hilsen Hello\""
                    + "  }"
                    + "}";
        } else {
            return "{"
                    + "  \"greeting\": \"Hello!\","
                    + "  \"country\": \"GB\","
                    + "  \"_links\": {"
                    + "    \"href\": \"/greetings/hello\","
                    + "    \"title\": \"English Greeting Hallo\""
                    + "  }"
                    + "}";
        }
    }

    private String validateOrCreateToken(String token) {
        if (token != null && !"".equals(token)) {
            return token;
        }
        return UUID.randomUUID().toString();
    }

    /**
     * @deprecated - use the endpoint above with application/hal+json to get list of greetings back. TERMINATE: Terminate as soon as early
     * consumers are not using it anymore
     */
    private Response handleBackwardsCompliance(String acceptLanguage, String responseEntity) {
        String language = preferredLanguage(acceptLanguage);
        if (language.contains("en")) {
            responseEntity = "{\"greeting\":\"Hello!\"}";
        }
        CacheControl cacheControl = new CacheControl();
        int maxAge = 60;
        cacheControl.setMaxAge(maxAge);
        return Response.ok()
                .entity(responseEntity)
                .type("application/json")
                .header("X-Status", "deprecated")
                .cacheControl(cacheControl)
                .build();
    }

    /**
     * using a non-mockable way to get time in an interval of 10 secs to showcase the last modified header so if you are doing this for real and
     * want to use time - pls use Instant and Clock
     */
    private Date getLastModified() {
        return Date.from(Instant.ofEpochMilli(1505500000000L));
    }

    interface GreetingProducer {

        Response getResponse(Request request, String accept, String language, String greeting, String logToken);
    }

    Response handle415Unsupported(Request request, String... params) {
        String msg = Arrays.toString(params);
        LOGGER.log(Level.INFO, "Attempted to get an nonsupported content type {0}", msg);
        return Response
                .status(Response.Status.UNSUPPORTED_MEDIA_TYPE)
                .build();
    }

}
