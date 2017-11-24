package com.example.service.patch;

import java.io.IOException;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;

/**
 * A JAX-RS container response filter that applies {@code Accept-Patch} header to 
 * any response to an {@code OPTIONS} request. Filter origins from the Jersey Sample on Patch.
 */
public class OptionsAcceptPatchHeaderFilter implements ContainerResponseFilter {

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
            throws IOException {

        if (HttpMethod.OPTIONS.equals(requestContext.getMethod())) {
            final MultivaluedMap<String, Object> headers = responseContext.getHeaders();
            if (!headers.containsKey("Accept-Patch")) {
                headers.putSingle("Accept-Patch", "application/patch+json");
            }
        }
    }
}

