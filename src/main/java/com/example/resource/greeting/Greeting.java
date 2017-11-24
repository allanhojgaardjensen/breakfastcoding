package com.example.resource.greeting;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.validation.constraints.Pattern;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PATCH;
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
import javax.ws.rs.core.UriInfo;

import com.example.RepresentationContainer;
import com.example.service.patch.JSONPatchContainer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.openapitools.jackson.dataformat.hal.HALLink;
import io.openapitools.jackson.dataformat.hal.HALMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Greetings Resource handles greetings in Danish and English.
 */
@Path("greetings")
@Api(value = "/greetings", tags = {"greetings"})
public class Greeting {

    private static final Logger LOGGER = Logger.getLogger(Greeting.class.getName());
    
    private static RepresentationContainer<String, GreetingRepresentation> representations = new RepresentationContainer<>();

    private final Map<String, GreetingProducer> greetingProducers = new HashMap<>();
    private final Map<String, GreetingListProducer> greetingListProducers = new HashMap<>();

    public Greeting() {
        populateRepresentations();
        greetingProducers.put("application/json", this::getGreetingG1V4);
        greetingProducers.put("application/hal+json", this::getGreetingG1V4);
        greetingProducers.put("application/hal+json;concept=greeting", this::getGreetingG1V4);
        greetingProducers.put("application/hal+json;concept=greeting;v=2", this::getGreetingG1V2);
        greetingProducers.put("application/hal+json;concept=greeting;v=3", this::getGreetingG1V3);
        greetingProducers.put("application/hal+json;concept=greeting;v=4", this::getGreetingG1V4);

        greetingListProducers.put("application/json", this::getGreetingListG1V2);
        greetingListProducers.put("application/hal+json", this::getGreetingListG1V2);
        greetingListProducers.put("application/hal+json;concept=greetings", this::getGreetingListG1V2);
        greetingListProducers.put("application/hal+json;concept=greetings;v=2", this::getGreetingListG1V2);
        greetingListProducers.put("application/hal+json;concept=greetings;v=1", this::getGreetingListG1V1);

    }

    /**
     * Create a new greeting and disallow replace an existing greeting.
     *
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
     *               "title": "Dansk Hilsen Halløj"
     * }
     * }
     * }
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
        Response.Status status = Response.Status.BAD_REQUEST;
        ObjectMapper mapper = new HALMapper();
        try {
            GreetingRepresentation mg = mapper.readValue(greeting, GreetingRepresentation.class);
            String key = getGreetingRef(mg) + "_" + preferredLanguage(acceptLanguage);
            GreetingRepresentation stored = representations.get(key);
            if (stored != null) {
                LOGGER.log(Level.INFO, "Attempted to update an existing Greeting (" + key + ") - in total (" + representations.size() + "):\n" + mg.toHAL());
                String errMsg = "{"
                        + "  \"message\": \"Sorry that your request for updating greeting could not be met!\","
                        + "  \"_links\":{"
                        + "      \"href\":\"/greetings/" + stored.getSelf().getHref() + "\","
                        + "      \"type\":\"application/hal+json\","
                        + "      \"title\":\"Update Greeting Resource\""
                        + "      }"
                        + "}";
                return Response
                        .status(Response.Status.CONFLICT)
                        .entity(errMsg)
                        .header("Location", stored.getSelf().getHref())
                        .header("X-Log-Token", validateOrCreateToken(logToken))
                        .build();
            }
            status = createNewGreeting(mg, mg.getSelf().getHref(), greeting, key, logToken + "problem creating new greeting");
            if (Response.Status.CREATED.equals(status)) {
                LOGGER.log(Level.INFO, "Parsed new Greeting (" + key + ") - in total (" + representations.size() + "):\n" + mg.toHAL());
                EntityTag et = getETag(mapper.writeValueAsString(representations.get(key)));
                return Response
                    .status(status)
                    .tag(et)
                    .header("Location", mg.getSelf().getHref())
                    .header("X-Log-Token", validateOrCreateToken(logToken))
                    .build();
            }
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Sorry, I could not parse the input. which was:\n" + greeting.toString(), ex);
        }
        return Response.status(status).build();
    }

    /**
     * A Greeting can be addressed specifically and the consumer can specify what language he/she prefers.
     * <p>
     * A LogToken can be part of the request and that will be returned in the response. If no LogToken is present in the request a new one is extracted and returned to the
     * consumer. The format for the LogToken is a 36 long string that can consist of a-z, A-Z,0-9 and - In other words: small letters, capital letters and numbers and hyphens
     * <p>
     * @param request the actual request
     * @param acceptLanguage the preferred language
     * @param accept the accepted response format
     * @param logToken a correlation id for a consumer
     * @param eTag the concrete instance of the lists contents version seen temporally
     * @return String that will be returned containing "application/hal+json".
     */
    @GET
    @Produces({"application/hal+json", "application/json"})
    @ApiOperation(value = "list all greetings", response = GreetingsRepresentation.class)
    public Response getGreetingsList(
            @Context Request request,
            @HeaderParam("Accept") String accept,
            @HeaderParam("Accept-Language")
            @Pattern(regexp = "^((\\s*[a-z]{2},{0,1}(-{0,1}[a-z]{2}){0,1})+(;q=0\\.[1-9]){0,1},{0,1})+") String acceptLanguage,
            @HeaderParam("X-Log-Token") @Pattern(regexp = "^[a-zA-Z0-9\\-]{36}$") String logToken,
            @HeaderParam("If-None-Match") String eTag) {
        return greetingListProducers.getOrDefault(accept, this::handle415Unsupported).getResponse(request, accept, acceptLanguage, logToken, eTag);
    }

    /**
     * Create a new greeting or replace an existing greeting.
     *
     * @param request the received request
     * @param acceptLanguage the preferred language
     * @param logToken a correlation id for a consumer
     * @param eTag the actual instance content version for a given greeting
     * @param greeting a json formatted input
     * @param resource the concrete resource
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
     * }
     * }
     * }
     * )}
     */
    @PUT
    @Path("{greeting}")
    @Produces({"application/hal+json"})
    @Consumes({"application/json"})
    @ApiOperation(value = "replace a greeting", response = GreetingRepresentation.class)
    public Response replaceOrCreateGreeting(
            @Context Request request,
            @HeaderParam("Accept-Language") @Pattern(regexp = "^((\\s*[a-z]{2},{0,1}(-{0,1}[a-z]{2}){0,1})+(;q=0\\.[1-9]){0,1},{0,1})+") String acceptLanguage,
            @HeaderParam("X-Log-Token") @Pattern(regexp = "^[a-zA-Z0-9\\-]{36}$") String logToken,
            @HeaderParam("If-None-Match") String eTag,
            @PathParam("greeting") @Pattern(regexp = "^[a-z0-9\\-]+$") String resource,
            String greeting) {
        ObjectMapper mapper = new HALMapper();
        try {
            GreetingRepresentation mappedGreeting = mapper.readValue(greeting, GreetingRepresentation.class);
            String key = getGreetingRef(mappedGreeting) + "_" + preferredLanguage(acceptLanguage);
            GreetingRepresentation stored = representations.get(key);
            final String msg = "Greeting (" + key + ") - in total (" + representations.size() + "):\n" + mappedGreeting.toHAL();
            final String inconsistency = "Href and ressource mismatch - target:" + resource + " object:" + msg;
            GreetingRepresentation receivedGreeting = new GreetingRepresentation(mappedGreeting);
            Response.Status status;
            EntityTag et = null;
            if (stored == null) {
                status = createNewGreeting(receivedGreeting, resource, msg, key, inconsistency);
                et = getETag(mapper.writeValueAsString(representations.get(key)));
            } else if (isRessourceIdCorrect(stored, resource)) {
                et = getETag(mapper.writeValueAsString(stored));
                ResponseBuilder builder = request.evaluatePreconditions(et);
                if (builder == null) {
                    return Response.status(Response.Status.CONFLICT)
                        .entity("{\"error\":\"object has been updated, please get newest version\"}")
                        .header("X-Log-Token", validateOrCreateToken(logToken))
                        .build();
                }
                status = replaceGreeting(msg, key, receivedGreeting);
                et = getETag(mapper.writeValueAsString(representations.get(key)));
            } else {
                LOGGER.log(Level.INFO, inconsistency);
                status = Response.Status.BAD_REQUEST;
            }
            return Response
                    .status(status)
                    .tag(et)
                    .header("Location", receivedGreeting.getSelf().getHref())
                    .header("X-Log-Token", validateOrCreateToken(logToken))
                    .build();
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Sorry, I could not parse the input. which was:\n" + greeting.toString(), ex);
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    /**
     * A Greeting can be deleted.
     * <p>
     * A LogToken can be part of the request and that will be returned in the response. If no LogToken is present in the request a new one is extracted and returned to the
     * consumer. The format for the LogToken is a 36 long string that can consist of a-z, A-Z,0-9 and - In other words: small letters, capital letters and numbers and hyphens
     * <p>
     * @param request the actual request received
     * @param accept the chosen accepted content-type by consumer
     * @param acceptLanguage client can set the preferred preferredLanguage(s) as in HTTP spec.
     * @param logToken a correlation id for a consumer
     * @param eTag the actual instance content version for a given greeting
     * @param greeting the greeting to delete.
     * @return status, headers etc. to consumer
     */
    @DELETE
    @Path("{greeting}")
    @Consumes({"application/json"})
    @ApiOperation(value = "delete a greeting")
    public Response deleteGreeting(
            @Context Request request,
            @HeaderParam("Accept") String accept,
            @HeaderParam("Accept-Language") @Pattern(regexp = "^((\\s*[a-z]{2},{0,1}(-{0,1}[a-z]{2}){0,1})+(;q=0\\.[1-9]){0,1},{0,1})+") String acceptLanguage,
            @HeaderParam("X-Log-Token") @Pattern(regexp = "^[a-zA-Z0-9\\-]{36}$") String logToken,
            @HeaderParam("If-None-Match") String eTag,
            @PathParam("greeting") @Pattern(regexp = "[a-z]*") String greeting) {
        String key = greeting + "_" + preferredLanguage(acceptLanguage);
        GreetingRepresentation stored = representations.get(key);
        ObjectMapper mapper = new HALMapper();
        Response.Status status;
        if (stored == null) {
            LOGGER.log(Level.INFO, "Attempted to delete a non-existing Greeting " + key);
            status = Response.Status.NOT_FOUND;
        } else {
            try {
                EntityTag et = getETag(mapper.writeValueAsString(stored));
                ResponseBuilder builder = request.evaluatePreconditions(et);
                if (builder == null) {
                    return Response.status(Response.Status.CONFLICT)
                        .entity("{\"error\":\"object has been updated, please get newest version\"}")
                        .header("X-Log-Token", validateOrCreateToken(logToken))
                        .build();
                }
            } catch (JsonProcessingException ex) {
                LOGGER.log(Level.WARNING, "Delete of object failed " + key);
            }
            LOGGER.log(Level.INFO, "Deleted " + key);
            status = Response.Status.NO_CONTENT;
            try {
                EntityTag et = getETag(mapper.writeValueAsString(representations.get(key)));
                representations.remove(key);
                LOGGER.log(Level.INFO, "Greetings " + representations.size());
                return Response
                    .status(status)
                    .tag(et)
                    .header("X-Log-Token", validateOrCreateToken(logToken))
                    .build();

            } catch (JsonProcessingException pe) {
                LOGGER.log(Level.WARNING, "Delete of object failed " + key);
            }
        }
        return Response
                .status(status)
                .header("X-Log-Token", validateOrCreateToken(logToken))
                .build();
    }

    /**
     * A Greeting can be updated.
     * <p>
     * A LogToken can be part of the request and that will be returned in the response. If no LogToken is present in the request a new one is extracted and returned to the
     * consumer. The format for the LogToken is a 36 long string that can consist of a-z, A-Z,0-9 and - In other words: small letters, capital letters and numbers and hyphens
     * <p>
     * @param request the actual request
     * @param accept the chosen accepted content-type by consumer
     * @param acceptLanguage client can set the preferred preferredLanguage(s) as in HTTP spec.
     * @param eTag which is the header "If-None-Match" the etag which sets the expected state for the greeting to be updated
     * @param logToken a correlation id for a consumer
     * @param greeting the greeting to update.
     * @param patch the patch that is used for updating the greeting
     * @return status, headers etc. to consumer
     */
    @PATCH
    @Path("{greeting}")
    @Consumes({"application/patch+json", "application/json"})
    @Produces({"application/json"})
    @ApiOperation(value = "update a greeting")
    public Response updateGreeting(
            @Context Request request,
            @HeaderParam("Accept") String accept,
            @HeaderParam("Accept-Language") @Pattern(regexp = "^((\\s*[a-z]{2},{0,1}(-{0,1}[a-z]{2}){0,1})+(;q=0\\.[1-9]){0,1},{0,1})+") String acceptLanguage,
            @HeaderParam("If-None-Match") String eTag,
            @HeaderParam("X-Log-Token") @Pattern(regexp = "^[a-zA-Z0-9\\-]{36}$") String logToken,
            @PathParam("greeting") @Pattern(regexp = "[a-z]*") String greeting,
            String patch) {
        String key = greeting + "_" + preferredLanguage(acceptLanguage);
        GreetingRepresentation stored = representations.get(key);
        if (stored == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            ObjectMapper om = new HALMapper();
            try {
                EntityTag et = getETag(om.writeValueAsString(stored));
                ResponseBuilder builder = request.evaluatePreconditions(et);
                if (builder != null) {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        JSONPatchContainer patchR = mapper.readValue(patch, JSONPatchContainer.class);
                        if (patchR.getOperation().equals("replace")) {
                            try {
                                if (!patchR.replaceValue(stored)) {
                                    return getPatchResponse(Response.Status.BAD_REQUEST, "{\"error\":\"value could not be replaced\"}",
                                            stored.getSelf().getHref(), logToken);
                                } else {
                                    representations.alterchCode();
                                }
                            } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException | SecurityException ex) {
                                return getPatchResponse(Response.Status.BAD_REQUEST, "{\"error\":\"value was not replaced\"}",
                                        stored.getSelf().getHref(), logToken);
                            }
                            return getPatchResponse(Response.Status.OK, "{\"status\":\"value is replaced\"}",
                                    stored.getSelf().getHref(), logToken);
                        } else {
                            return getPatchResponse(
                                    Response.Status.BAD_REQUEST, "{\"error\":\"only operation replace is supported\"}",
                                    stored.getSelf().getHref(), logToken);
                        }
                    } catch (IOException ex) {
                        Response.status(Response.Status.BAD_REQUEST).build();
                    }
                } else {
                    return getPatchResponse(
                            Response.Status.CONFLICT, "{\"error\":\"object has been updated, please get newest version\"}",
                            stored.getSelf().getHref(), logToken);
                }
            } catch (JsonProcessingException ex) {
                LOGGER.log(Level.SEVERE, "Could not map List to json", ex);           
            }  
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    /**
     * A Greeting can be addressed specifically and the consumer can specify what language he/she prefers.
     * <p>
     * A LogToken can be part of the request and that will be returned in the response. If no LogToken is present in the request a new one is extracted and returned to the
     * consumer. The format for the LogToken is a 36 long string that can consist of a-z, A-Z,0-9 and - In other words: small letters, capital letters and numbers and hyphens
     * <p>
     * @param request the actual request
     * @param uriInfo the URI information
     * @param accept the chosen accepted content-type by consumer
     * @param acceptLanguage client can set the preferred preferredLanguage(s) as in HTTP spec.
     * @param logToken a correlation id for a consumer
     * @param eTag the version of the list, it changes every time the list is changed
     * @param greeting the greeting wanted by consumer
     * @return String that will be returned containing "application/hal+json".
     */
    @GET
    @Path("{greeting}")
    @Produces({"application/json", "application/hal+json"})
    @ApiOperation(value = "get a greeting", response = GreetingRepresentation.class)
    public Response getGreeting(
            @Context Request request, @Context UriInfo uriInfo,
            @HeaderParam("Accept") String accept,
            @HeaderParam("Accept-Language") @Pattern(regexp = "^((\\s*[a-z]{2},{0,1}(-{0,1}[a-z]{2}){0,1})+(;q=0\\.[1-9]){0,1},{0,1})+") String acceptLanguage,
            @HeaderParam("X-Log-Token") @Pattern(regexp = "^[a-zA-Z0-9\\-]{36}$") String logToken,
            @HeaderParam("If-None-Match") String eTag,
            @PathParam("greeting") @Pattern(regexp = "[a-z]*") String greeting) {
        return greetingProducers.getOrDefault(accept, this::handle415Unsupported).getResponse(request, accept, acceptLanguage, greeting, logToken);
    }

    private Response getGreetingListG1V2(Request request, String accept, String acceptLanguage, String logToken, String eTag) {
        EntityTag et = getETag(representations.getChCode());
        ResponseBuilder builder = request.evaluatePreconditions(et);
        if (builder != null) {
            return builder.build();
        }
        CacheControl cacheControl = new CacheControl();
        int maxAge = 30;
        cacheControl.setMaxAge(maxAge);
        int version = 2;
        Collection<GreetingRepresentation> greetingsList = representations.values()
                .stream()
                .map(gr -> new GreetingRepresentation(gr))
                .collect(Collectors.toList());
        GreetingsRepresentation gr = new GreetingsRepresentation("This is the information v2HAL", greetingsList);
        ObjectMapper halMapper = new HALMapper();
        String json = "{\"error\":\"could not parse object\"}";
        try {
            json = halMapper.writeValueAsString(gr);
        } catch (JsonProcessingException ex) {
            LOGGER.log(Level.SEVERE, "Could not map List to json", ex);
        }
        return Response.ok()
                .entity(json)
                .tag(et)
                .type("application/hal+json;concept=greetings;v=" + version)
                .cacheControl(cacheControl)
                .header("X-Log-Token", validateOrCreateToken(logToken))
                .build();
    }

    private Response getGreetingListG1V1(Request request, String accept, String acceptLanguage, String logToken, String eTag) {
        CacheControl cacheControl = new CacheControl();
        int maxAge = 30;
        int version = 1;
        cacheControl.setMaxAge(maxAge);
        return Response.ok()
                .entity(getGreetingList(version))
                .type("application/hal+json;concept=greetings;v=" + version)
                .cacheControl(cacheControl)
                .header("X-Log-Token", validateOrCreateToken(logToken))
                .build();
    }

    /**
     * Implements latest version of the greeting service.
     * <p>
     * The consumer roll back by entering the full content-type in the Accept header in this case {@code application/json;concept=greeting;v=1} or more specific and correct as that
     * is the actual format used. {@code application/hal+json;concept=greeting;v=1}
     */
    private Response getGreetingG1V4(Request request, String accept, String acceptLanguage, String greeting, String logToken) {
        String language = preferredLanguage(acceptLanguage);
        final String key = greeting + "_" + language;
        GreetingRepresentation entity = representations.get(key);
        if (entity == null) {
            return getNoGreetingFound(logToken);
        }
        ObjectMapper mapper = new HALMapper();
        Date lastModified = getLastModified();
        EntityTag eTag = getETag(entity.toString());
        String entityResponse = entity.toString();
        try {
            eTag = getETag(mapper.writeValueAsString(entity));
            entityResponse = mapper.writeValueAsString(entity);
        } catch (JsonProcessingException ex) {
            LOGGER.log(Level.WARNING, "Could not map entity:\n " + entity.toString(), ex);
        }
        ResponseBuilder builder = request.evaluatePreconditions(lastModified, eTag);
        if (builder != null) {
            return builder.build();
        }
        CacheControl cacheControl = new CacheControl();
        int maxAge = 60;
        cacheControl.setMaxAge(maxAge);
        return Response.ok()
                .entity(entityResponse)
                .type("application/hal+json;concept=greeting;v=4")
                .cacheControl(cacheControl)
                .tag(eTag)
                .lastModified(lastModified)
                .header("X-Log-Token", validateOrCreateToken(logToken))
                .build();
    }

    private Response getGreetingG1V3(Request request, String accept, String acceptLanguage, String greeting, String logToken) {
        String language = preferredLanguage(acceptLanguage);
        GreetingRepresentation greetingEntity = representations.get(greeting + "_" + language);
        if (greetingEntity == null) {
            return getNoGreetingFound(logToken);
        }
        return getResponse(request, logToken, greetingEntity.toHAL(), 3);
    }

    private Response getGreetingG1V2(Request request, String accept, String acceptLanguage, String greeting, String logToken) {
        String language = preferredLanguage(acceptLanguage);
        GreetingRepresentation greetingEntity = representations.get(greeting + "_" + language);
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

    private Response.Status replaceGreeting(final String msg, String key, GreetingRepresentation receivedGreeting) {
        Response.Status status;
        LOGGER.log(Level.INFO, "Parsed Replaceable ", msg);
        status = Response.Status.OK;
        representations.add(key, receivedGreeting);
        return status;
    }

    private Response.Status createNewGreeting(GreetingRepresentation receivedGreeting, String resource, final String msg, String key, final String inconsistency) {
        Response.Status status;
        if (isRessourceIdCorrect(receivedGreeting, resource)) {
            LOGGER.log(Level.INFO, "Parsed New ", msg);
            GreetingRepresentation newGreeting = new GreetingRepresentation(receivedGreeting);
            status = Response.Status.CREATED;
            representations.add(key, newGreeting);
        } else {
            LOGGER.log(Level.INFO, inconsistency, msg);
            status = Response.Status.BAD_REQUEST;
        }
        return status;
    }

    private Response getPatchResponse(Response.Status status, String entity, String href, String logToken) {
        return Response
                .status(status)
                .entity(entity)
                .header("Location", href)
                .header("X-Log-Token", validateOrCreateToken(logToken))
                .build();
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
        EntityTag eTag = getETag(entity);
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

    private String getGreetingRef(GreetingRepresentation newGreeting) {
        String ref = newGreeting.getSelf().getHref();
        String resources = "greetings/";
        int start = ref.indexOf(resources) + resources.length();
        String result = ref.substring(start).toLowerCase();
        return result;
    }

    private String getGreetingList(int version) {
        final String template = "{"
                + "\"greetings\":{"
                + "\"info\":\"a list containing current greetings\","
                + "\"_links\":{"
                + "\"self\":{"
                + "\"href\":\"/greetings\","
                + "\"type\":\"application/hal+json;concept=greetinglist;v=" + version + "\","
                + "\"title\":\"List of Greetings\""
                + "},"
                + "\"greetings\":"
                + "["
                + getResultingGreetingsList()
                + "]"
                + "}"
                + "}"
                + "}";
        return template;
    }

    private String getResultingGreetingsList() {
        String result;
        StringBuilder list = new StringBuilder();
        representations
                .entrySet()
                .stream()
                .map((entry) -> list
                .append("{\"href\":\"")
                .append(entry.getValue().getSelf().getHref())
                .append("\",\"title\":\"")
                .append(entry.getValue().getSelf().getTitle())
                .append("\"},"))
                .collect(Collectors.joining());
        result = list.substring(0, list.length() - 1);
        return result;
    }

    private boolean isRessourceIdCorrect(GreetingRepresentation greeting, String resource) {
        return greeting.getSelf().getHref().contains(resource);
    }

    /**
     * using a non-mockable way to get time in an interval of 10 secs to showcase the last modified header so if you are doing this for real and want to use time - pls use Instant
     * and Clock
     */
    private Date getLastModified() {
        return Date.from(Instant.ofEpochMilli(1505500000000L));
    }

    private EntityTag getETag(String entity) {
        if (entity == null) entity = representations.getChCode();
        return new EntityTag(Integer.toHexString(entity.hashCode()), false);
    }

    private void populateRepresentations() {
        if (representations.isEmpty()) {
            HALLink self = new HALLink.Builder("/greetings/hallo")
                .title("Dansk Hilsen Hallo")
                .seen(Instant.now())
                .name("Danish Greeting Hallo")
                .templated(false)
                .hreflang("da")    
                .type("application/hal+json;concept=greeting")
                .build();
            representations.add("hallo_da", 
                new GreetingRepresentation("Hallo!", "Dansk", "Danmark", 
                    new GreetingNativeRepresentation("Dansk", "Danmark"), self));

            self = new HALLink.Builder("/greetings/hallo")
                .title("Danish Greeting Hallo")
                .seen(Instant.now())
                .name("Danish Greeting Hallo")
                .templated(false)
                .hreflang("en")    
                .type("application/hal+json;concept=greeting")
                .build();
            representations.add("hallo_en", 
                new GreetingRepresentation("Hallo!", "Dansk", "Danmark", 
                    new GreetingNativeRepresentation("Danish", "Denmark"), self));

            self = new HALLink.Builder("/greetings/hello")
                .title("Engelsk Hilsen Hello")
                .seen(Instant.now())
                .name("English Greeting Hello")
                .templated(false)
                .hreflang("da")    
                .type("application/hal+json;concept=greeting")
                .build();
            representations.add("hello_da", 
                new GreetingRepresentation("Hello!", "English", "England", 
                    new GreetingNativeRepresentation("Engelsk", "England"), self));
        
            self = new HALLink.Builder("/greetings/hello")
                .title("English Greeting Hello")
                .seen(Instant.now())
                .name("English Greeting Hello")
                .templated(false)
                .hreflang("en")    
                .type("application/hal+json;concept=greeting")
                .build();
            representations.add("hello_en", 
                new GreetingRepresentation("Hello!", "English", "England", 
                    new GreetingNativeRepresentation("English", "England"), self));
            LOGGER.log(Level.INFO, "Default data bootstrap activated", representations.size());
        }
    }

    interface GreetingProducer {
        Response getResponse(Request request, String accept, String language, String greeting, String logToken);
    }

    interface GreetingListProducer {
        Response getResponse(Request request, String accept, String language, String logToken, String eTag);
    }

    Response handle415Unsupported(Request request, String... params) {
        String msg = Arrays.toString(params);
        LOGGER.log(Level.INFO, "Attempted to get an nonsupported content type {0}", msg);
        return Response
                .status(Response.Status.UNSUPPORTED_MEDIA_TYPE)
                .build();
    }
}
