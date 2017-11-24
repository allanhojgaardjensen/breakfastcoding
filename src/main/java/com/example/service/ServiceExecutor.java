package com.example.service;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.ext.ContextResolver;

import com.example.filter.CORSFilter;
import com.example.service.patch.OptionsAcceptPatchHeaderFilter;
import com.example.service.patch.PatchInterceptor;
import io.swagger.annotations.SwaggerDefinition;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * ServiceExecutor - allows execution using 'mvn exec:java@start-server' 
 */
@SwaggerDefinition(
        host = "greetings.services.example.com",
        basePath = "/",
        produces = "application/hal+json",
        consumes = "application/json",
        schemes = SwaggerDefinition.Scheme.HTTPS
)


@ApplicationPath("/")
public final class ServiceExecutor {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://0.0.0.0:8080/";
    private static final Logger LOGGER = Logger.getLogger(ServiceExecutor.class.getName());

    private ServiceExecutor() {
        // reduced constructor scope.
    }
    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), create());
    }

    /**
     * ServiceExecutor method
     * @param args is part of main signature, however no args are used in main.
     * @throws Exception if anything fails. 
     */
    public static void main(String[] args) throws Exception {
        final HttpServer server = startServer();
        LOGGER.log(Level.INFO, String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl%nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.shutdownNow();
    }
    
    public static ResourceConfig create() {
        final ResourceConfig rc = new ResourceConfig(OptionsAcceptPatchHeaderFilter.class, PatchInterceptor.class)
                .packages("com.example")
                .register(CORSFilter.class);
        rc.register(createMoxyJsonResolver());
        rc.property("jersey.config.server.tracing.type", "ON_DEMAND");
        return rc;
    }

    
    /**
     * Create {@link javax.ws.rs.ext.ContextResolver} for {@link org.glassfish.jersey.moxy.json.MoxyJsonConfig}
     * for this application.
     *
     * @return {@code MoxyJsonConfig} context resolver.
     */
    public static ContextResolver<MoxyJsonConfig> createMoxyJsonResolver() {
        final MoxyJsonConfig moxyJsonConfig = new MoxyJsonConfig()
                .setFormattedOutput(true)
                .setNamespaceSeparator(':');
        return moxyJsonConfig.resolver();
    }
}

