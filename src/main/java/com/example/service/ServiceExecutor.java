package com.example.service;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.filter.CORSFilter;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * ServiceExecutor - allows execution using 'mvn exec:java' 
 */
public class ServiceExecutor {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/";
    private static final Logger LOGGER = Logger.getLogger(ServiceExecutor.class.getName());

    private ServiceExecutor() {
        // reduced constructor scope.
    }
    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig()
                .packages("com.example")
                .register(CORSFilter.class);
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * ServiceExecutor method
     * @param args is part of main signature, however no args are used in main.
     * @throws Exception if anything fails. 
     */
    public static void main(String[] args) throws Exception {
        final HttpServer server = startServer();
        LOGGER.log(Level.INFO, String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.shutdownNow();
    }
}

