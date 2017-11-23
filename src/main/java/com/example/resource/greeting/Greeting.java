package com.example.resource.greeting;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.validation.constraints.Pattern;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Greetings Resource handles greetings in Danish and English.
 */
@Path("greetings")
@Api(value = "/greetings", tags = {"greetings"})
public class Greeting {

    private static final Logger LOGGER = Logger.getLogger(Greeting.class.getName());
    private static final Map<String, GreetingRepresentation> REPRESENTATIONS = new ConcurrentHashMap<>();
    private final Map<String, GreetingProducer> greetingProducers = new HashMap<>();
    public Greeting() {
        greetingProducers.put("application/json", this::getGreetingG1V3);
        greetingProducers.put("application/hal+json", this::getGreetingG1V3);
        greetingProducers.put("application/hal+json;concept=greeting", this::getGreetingG1V3);
        greetingProducers.put("application/hal+json;concept=greeting;v=2", this::getGreetingG1V2);
        greetingProducers.put("application/hal+json;concept=greeting;v=3", this::getGreetingG1V3);

        //populate the version 3 setup
        REPRESENTATIONS.put("hallo_da",
                new GreetingRepresentation("Hallo!", "Dansk", "Danmark", "Dansk", "Danmark", "greetings/hallo", "Dansk Hilsen Hallo"));
        REPRESENTATIONS.put("hallo_en",
                new GreetingRepresentation("Hallo!", "Dansk", "Danmark", "Danish", "Denmark", "greetings/hallo", "Danish Greeting Hallo"));
        REPRESENTATIONS.put("hello_da",
                new GreetingRepresentation("Hello!", "English", "England", "Engelsk", "England", "greetings/hello", "Engelsk Hilsen Hello"));
        REPRESENTATIONS.put("hello_en",
                new GreetingRepresentation("Hello!", "English", "England", "English", "England", "greetings/hello", "English Greeting Hello"));
    }

    /**
     * Create a new greeting and disallow replace an existing greeting.
     * @param request the actual request
     * @param acceptLanguage the preferred language
     * @param logToken a correlation id for a consumer
     * @param greeting a json formatted input 
     * @return response the status, headers etc. send back to the consumer
     * 
     * {@code( 
     *   {
     *       "greeting": "Halløj!",
     *       "language": "Dansk",
     *       "country": "Danmark",
     *       "native": {
     *           "language": "Dansk",
     *           "country": "Danmark"
     *       },
     *       "_links": {
     *           "self": {
     *               "href": "greetings/halloj",
     *               "title": "Dansk Hilsen Halløj osv"
     *           }
     *       }
     *   }
     * )}
     */
    @POST
    @Produces({"application/hal+json"})
    @Consumes({"application/json"})
    @ApiOperation(value = "create a new greeting")
    public Response createNewGreeting(
            @Context Request request,
            @HeaderParam("Accept-Language") @Pattern(regexp = "^((\\s*[a-z]{2},{0,1}(-{0,1}[a-z]{2}){0,1})+(;q=0\\.[1-9]){0,1},{0,1})+") String acceptLanguage,
            @HeaderParam("X-Log-Token") @Pattern(regexp = "^[a-zA-Z0-9\\-]{36}$") String logToken,
            String greeting) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            GreetingRepresentation newGreeting = mapper.readValue(greeting, GreetingRepresentation.class);
            String key = getGreetingRef(newGreeting) + "_" + preferredLanguage(acceptLanguage);
            GreetingRepresentation stored = REPRESENTATIONS.get(key);
            if (stored != null) {
                LOGGER.log(Level.INFO, "Attempted to update an existing Greeting (" + key + ") - in total (" + REPRESENTATIONS.size() + "):\n" + newGreeting.toHAL());
                String errMsg = "{"
                        + "  \"message\": \"Sorry that your request for updating greeting could not be met!\","
                        + "  \"_links\":{"
                        + "      \"href\":\"/greetings/" + stored.getLinks().getSelf().getHref() + "\","
                        + "      \"type\":\"application/hal+json\","
                        + "      \"title\":\"Update Greeting Resource\""
                        + "      }"
                        + "}";
                return Response
                        .status(Response.Status.CONFLICT)
                        .entity(errMsg)
                        .header("Location", stored.getLinks().getSelf().getHref())
                        .header("X-Log-Token", validateOrCreateToken(logToken))
                        .build();
                
            }
            REPRESENTATIONS.put(key, newGreeting);
            LOGGER.log(Level.INFO, "Parsed new Greeting (" + key + ") - in total (" + REPRESENTATIONS.size() + "):\n" + newGreeting.toHAL());
            return Response
                    .status(Response.Status.CREATED)
                    .header("Location", newGreeting.getLinks().getSelf().getHref())
                    .header("X-Log-Token", validateOrCreateToken(logToken))
                    .build();
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Sorry, I could not parse the input. which was:\n" + greeting.toString(), ex);
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    /**
     * A Greeting can be addressed specifically and the consumer can specify what language he/she prefers.
     * <p>
     * A LogToken can be part of the request and that will be returned in the response. If no LogToken is present in the request a new one is extracted and returned to the
     * consumer. The format for the LogToken is a 36 long string that can consist of a-z, A-Z,0-9 and - In other words: small letters, capital letters and numbers and hyphens
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
        CacheControl cacheControl = new CacheControl();
        int maxAge = 30;
        cacheControl.setMaxAge(maxAge);
        return Response.ok()
                .entity(getGreetingList())
                .type("application/hal+json;concept=greetings;v=1")
                .cacheControl(cacheControl)
                .header("X-Log-Token", validateOrCreateToken(logToken))
                .build();
    }

    /**
     * Create a new greeting or replace an existing greeting.
     * @param request the actual request
     * @param acceptLanguage the preferred language
     * @param logToken a correlation id for a consumer
     * @param greeting a json formatted input
     * @return response the status, headers etc. for consumer
     * 
     * {@code( 
     *   {
     *       "greeting": "Halløj!",
     *       "language": "Dansk",
     *       "country": "Danmark",
     *       "native": {
     *           "language": "Dansk",
     *           "country": "Danmark"
     *       },
     *       "_links": {
     *           "self": {
     *               "href": "greetings/halloj",
     *               "title": "Dansk Hilsen Halløj osv"
     *           }
     *       }
     *   }
     * )}
     */

    @PUT
    @Path("{greeting}")
    @Produces({"application/hal+json"})
    @Consumes({"application/json"})
    @ApiOperation(value = "replaces a greeting")
    public Response replaceOrCreateGreeting(
            @Context Request request,
            @HeaderParam("Accept-Language") @Pattern(regexp = "^((\\s*[a-z]{2},{0,1}(-{0,1}[a-z]{2}){0,1})+(;q=0\\.[1-9]){0,1},{0,1})+") String acceptLanguage,
            @HeaderParam("X-Log-Token") @Pattern(regexp = "^[a-zA-Z0-9\\-]{36}$") String logToken,
            String greeting) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            GreetingRepresentation receivedGreeting = mapper.readValue(greeting, GreetingRepresentation.class);
            String key = getGreetingRef(receivedGreeting) + "_" + preferredLanguage(acceptLanguage);
            GreetingRepresentation stored = REPRESENTATIONS.get(key);
            String msg = "Greeting (" + key + ") - in total (" + REPRESENTATIONS.size() + "):\n" + receivedGreeting.toHAL();
            Response.Status status;
            if (stored == null) {
                LOGGER.log(Level.INFO, "Parsed New ", msg);
                status = Response.Status.CREATED;
            } else {
                LOGGER.log(Level.INFO, "Parsed Replaceable ", msg);
                status = Response.Status.OK;
            }
            REPRESENTATIONS.put(key, receivedGreeting);
            return Response
                    .status(status)
                    .header("Location", receivedGreeting.getLinks().getSelf().getHref())
                    .header("X-Log-Token", validateOrCreateToken(logToken))
                    .build();
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Sorry, I could not parse the input. which was:\n" + greeting.toString(), ex);
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    /**
     * A Greeting can be addressed specifically and the consumer can specify what language he/she prefers.
     * <p>
     * A LogToken can be part of the request and that will be returned in the response. If no LogToken is present in the request a new one is extracted and returned to the
     * consumer. The format for the LogToken is a 36 long string that can consist of a-z, A-Z,0-9 and - In other words: small letters, capital letters and numbers and hyphens
     * <p>
     * @param request the actual http request
     * @param accept the chosen accepted content-type by consumer
     * @param acceptLanguage client can set the preferred preferredLanguage(s) as in HTTP spec.
     * @param logToken a correlation id for a consumer
     * @param greeting the greeting wanted by consumer
     * @return String that will be returned containing "application/hal+json".
     */
    @GET
    @Path("{greeting}")
    @Produces({"application/json", "application/hal+json"})
    @ApiOperation(value = "get a greeting back with a preferred language portion included", response = GreetingRepresentation.class)
    public Response getGreeting(
            @Context Request request,
            @HeaderParam("Accept") String accept,
            @HeaderParam("Accept-Language") @Pattern(regexp = "^((\\s*[a-z]{2},{0,1}(-{0,1}[a-z]{2}){0,1})+(;q=0\\.[1-9]){0,1},{0,1})+") String acceptLanguage,
            @HeaderParam("X-Log-Token") @Pattern(regexp = "^[a-zA-Z0-9\\-]{36}$") String logToken,
            @PathParam("greeting") @Pattern(regexp = "[a-z]*") String greeting) {
        return greetingProducers.getOrDefault(accept, this::handle415Unsupported).getResponse(request, accept, acceptLanguage, greeting, logToken);
    }

    /**
     * Implements version one of the greeting service, where detailed information needs to be handled and returned to consumer, this construction using interface and explicitly
     * mapping content-types to methods allows to maintain multiple content versions in same service endpoint and thus be able to ensure that consumers can roll back to this
     * version once the next edition that is no longer compliant is available.
     * <p>
     * The consumer roll back by entering the full content-type in the Accept header in this case {@code application/json;concept=greeting;v=1} or more specific and correct as that
     * is the actual format used. {@code application/hal+json;concept=greeting;v=1}
     */
    private Response getGreetingG1V3(Request request, String accept, String acceptLanguage, String greeting, String logToken) {
        String language = preferredLanguage(acceptLanguage);
        GreetingRepresentation greetingEntity = REPRESENTATIONS.get(greeting + "_" + language);
        if (greetingEntity == null) {
            return getNoGreetingFound(logToken);
        }
        String entity = greetingEntity.toHAL();
        return getResponse(request, logToken, entity, 3);
    }

    /**
     * Implements version two of the greeting service, where detailed information needs to be handled and returned to consumer, this construction using interface and explicitly
     * mapping content-types to methods allows to maintain multiple content versions in same service endpoint and thus be able to ensure that consumers can roll back to this
     * version once the next edition that is no longer compliant is available.
     * <p>
     * The consumer roll back by entering the full content-type in the Accept header in this case {@code application/json;concept=greeting;v=1} or more specific and correct as that
     * is the actual format used. {@code application/hal+json;concept=greeting;v=1}
     */
    private Response getGreetingG1V2(Request request, String accept, String acceptLanguage, String greeting, String logToken) {
        String language = preferredLanguage(acceptLanguage);
        GreetingRepresentation greetingEntity = REPRESENTATIONS.get(greeting + "_" + language);
        if (greetingEntity == null) {
            String entity = "{"
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
        return getResponse(request, logToken, greetingEntity.toHATEOAS(), 2);
    }

    private String preferredLanguage(String preferred) {
        if (preferred == null || preferred.isEmpty()) {
            return "da";
        }
        String[] languages = preferred.split(",");
        String[] preferredLanguage = Arrays.stream(languages).filter(s -> !s.contains(";")).toArray(String[]::new);
        return preferredLanguage[0];
    }

    private String validateOrCreateToken(String token) {
        if (token != null && !"".equals(token)) {
            return token;
        }
        return UUID.randomUUID().toString();
    }

    private Response getResponse(Request request, String logToken, String entity, int version) {
        Date lastModified = getLastModified();
        EntityTag eTag = new EntityTag(Integer.toHexString(entity.hashCode()), false);
        ResponseBuilder builder = request.evaluatePreconditions(lastModified, eTag);
        if (builder != null) {
            return builder.build();
        }
        CacheControl cacheControl = new CacheControl();
        int maxAge = 60;
        cacheControl.setMaxAge(maxAge);
        return Response
                .ok(entity)
                .type("application/hal+json;concept=greeting;v=" + version)
                .cacheControl(cacheControl)
                .tag(eTag)
                .lastModified(lastModified)
                .header("X-Log-Token", validateOrCreateToken(logToken))
                .build();
    }

    private Response getNoGreetingFound(String logToken) {
        String entity;
        entity = "{"
                + "\"message\":\"Sorry your greeting does not exist yet!\","
                + "\"_links\":{"
                + "\"greetings\":{"
                + "\"href\":\"/greetings\","
                + "\"type\":\"application/hal+json\","
                + "\"title\":\"List of exixting greetings\""
                + "}"
                + "}"
                + "}";
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(entity)
                .type("application/hal+json")
                .header("X-Log-Token", validateOrCreateToken(logToken))
                .build();
    }

    /**
     * using a non-mockable way to get time in an interval of 10 secs to showcase the last modified header so if you are doing this for real and want to use time - pls use Instant
     * and Clock
     */
    private Date getLastModified() {
        return Date.from(Instant.ofEpochMilli(1505500000000L));

    }

    private String getGreetingRef(GreetingRepresentation newGreeting) {
        String ref = newGreeting.getLinks().getSelf().getHref();
        String resources = "greetings/";
        int start = ref.indexOf(resources) + resources.length();
        String result = ref.substring(start).toLowerCase();
        return result;
    }

    private String getGreetingList() {
        final String template = "{"
                + "\"greetings\":{"
                + "\"info\":\"a list containing current greetings\","
                + "\"_links\":{"
                + "\"self\":{"
                +   "\"href\":\"/greetings\","
                +   "\"type\":\"application/hal+json;concept=greetinglist;v=1\","
                +   "\"title\":\"List of Greetings\""
                +   "},"
                + "\"greetings\":"
                +     "["
                + getResultingGreetingsList()
                +     "]"
                +   "}"
                + "}"
                + "}";
        return template;
    }

    private String getResultingGreetingsList() {
        String result;
        StringBuilder list = new StringBuilder();
        REPRESENTATIONS
                .entrySet()
                .stream()
                .map((entry) -> list
                        .append("{\"href\":\"")
                        .append(entry.getValue().getLinks().getSelf().getHref())
                        .append("\",\"title\":\"")
                        .append(entry.getValue().getLinks().getSelf().getTitle())
                        .append("\"},"))
                .collect(Collectors.joining());
        result = list.substring(0, list.length() - 1);
        return result;
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
