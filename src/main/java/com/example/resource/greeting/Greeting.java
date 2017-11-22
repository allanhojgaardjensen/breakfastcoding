package com.example.resource.greeting;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.validation.constraints.Pattern;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Greetings Resource handles greetings in Danish and English.
 */
@Path("greetings")
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
     * Method handling HTTP GET requests.
     *
     * @return String that will be returned containing "application/hal+json".
     */
    @GET
    @Produces({"application/hal+json"})
    public Response getGreetingsList() {
        final String greetingList
                = "{"
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
        return Response.ok(greetingList).build();
    }

    /**
     * Method handling HTTP GET requests.
     *
     * @param acceptLanguage client can set the preferred preferredLanguage(s) as in HTTP spec.
     * @deprecated this method will be removed over time in favor of the more semantically correct getGreetings.
     * @return String that will be returned containing "application/json".
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Deprecated
    public String getGreeting(@HeaderParam("Accept-Language")
            @Pattern(regexp = "^((\\s*[a-z]{2},{0,1}(-{0,1}[a-z]{2}){0,1})+(;q=0\\.[1-9]){0,1},{0,1})+") 
            String acceptLanguage) {
        String language = preferredLanguage(acceptLanguage);
        if (language.contains("en")) {
            return "{\"greeting\":\"Hello!\"}";
        }
        return "{\"greeting\":\"Hallo!\"}";
    }

    /**
     * Method handling HTTP GET requests.
     *
     * @param accept the chosen accepted content-type by consumer
     * @param acceptLanguage client can set the preferred preferredLanguage(s) as in HTTP spec.
     * @param greeting the greeting wanted by consumer
     * @return String that will be returned containing "application/hal+json".
     */
    @GET
    @Path("{greeting}")
    @Produces({"application/hal+json"})
    public Response getGreeting(
            @HeaderParam("Accept") String accept,
            @HeaderParam("Accept-Language") @Pattern(regexp = "^((\\s*[a-z]{2},{0,1}(-{0,1}[a-z]{2}){0,1})+(;q=0\\.[1-9]){0,1},{0,1})+") String acceptLanguage,
            @PathParam("greeting") @Pattern(regexp = "[a-z]*") String greeting) {
        return greetingProducers.getOrDefault(accept, this::handle415Unsupported).getResponse(accept, acceptLanguage, greeting);
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
    private Response getGreetingG1V1(String accept, String acceptLanguage, String greeting) {
        String language = preferredLanguage(acceptLanguage);
        String greet = getGreetingPathParam(greeting);
        switch (greet) {
            case "hallo":
                return Response
                        .ok(getDanish(language))
                        .type("application/hal+json;concept=greeting;v=1")
                        .build();
            case "hello":
                return Response
                        .status(200)
                        .type("application/hal+json;concept=greeting;v=1")
                        .entity(getEnglish(language))
                        .build();
            default:
                return Response
                        .status(404)
                        .type(MediaType.APPLICATION_JSON)
                        .entity("{"
                                + "  \"message\": \"Sorry your greeting does not exist yet!\","
                                + "  \"_links\":{"
                                + "      \"href\":\"/greetings\","
                                + "      \"type\":\"application/hal+json\","
                                + "      \"title\":\"List of exixting greetings\""
                                + "      }"
                                + "}")
                        .build();
        }
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
    private Response getGreetingG1V2(String accept, String acceptLanguage, String greeting) {
        String language = preferredLanguage(acceptLanguage);
        String greet = getGreetingPathParam(greeting);
        switch (greet) {
            case "hallo":
                return Response
                        .ok(getDanishFull(language))
                        .type("application/hal+json;concept=greeting;v=2")
                        .build();
            case "hello":
                return Response
                        .status(200)
                        .type("application/hal+json;concept=greeting;v=2")
                        .entity(getEnglishFull(language))
                        .build();
            default:
                return Response
                        .status(404)
                        .type(MediaType.APPLICATION_JSON)
                        .entity("{"
                                + "  \"message\": \"Sorry your greeting does not exist yet!\","
                                + "  \"_links\":{"
                                + "      \"href\":\"/greetings\","
                                + "      \"type\":\"application/hal+json\","
                                + "      \"title\":\"List of exixting greetings\""
                                + "      }"
                                + "}")
                        .build();
        }
    }

    private String getGreetingPathParam(String greeting) {
        if (greeting == null || greeting.isEmpty()) {
            return "hallo";
        }
        return greeting;
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

    interface GreetingProducer {

        Response getResponse(String accept, String language, String greeting);
    }

    Response handle415Unsupported(String... params) {
        return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).build();
    }

}
