package com.example;

import java.util.Arrays;

import javax.validation.constraints.Pattern;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Greetings Resource
 */
@Path("greetings")
public class Greeting {
    
    /**
     * Method handling HTTP GET requests.
     *
     * The returned object will be sent back as "application/json" media type.
     *
     * @param acceptLanguage client can set the preferred preferredLanguage(s) as in HTTP spec.
     * @return String that will be returned containing "application/json".
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getGreeting(@HeaderParam("Accept-Language") 
            @Pattern(regexp = "^((\\s*[a-z]{2},{0,1}(-{0,1}[a-z]{2}){0,1})+(;q=0\\.[1-9]){0,1},{0,1})+") 
            String acceptLanguage) {
        String language = preferredLanguage(acceptLanguage);
        if (language.contains("en")) {
            return "{\"greeting\":\"Hello!\"}";
        }
        return "{\"greeting\":\"Hallo!\"}";
    }

    private String preferredLanguage(String preferred) {
        if (preferred == null || preferred.isEmpty()) {
            return "da";
        }
        String[] languages = preferred.split(",");
        String[] preferredLanguage = Arrays.stream(languages).filter(s -> !s.contains(";")).toArray(String[]::new);
        return preferredLanguage[0];
    }
}
