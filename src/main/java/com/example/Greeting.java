package com.example;

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
     * @param acceptLanguage client can set the preferred language(s) as in HTTP spec.
     * @return String that will be returned containing "application/json".
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getGreeting(@HeaderParam("Accept-Language") String acceptLanguage) {
        return "{\"greeting\":\"Hallo!\"}";
    }
}
