package com.example.filter;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;

/**
 * a general CORS filter that allows everything from *
 */
public class CORSFilter implements ContainerResponseFilter {

    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, PATCH, OPTIONS, HEAD");
        headers.add("Access-Control-Allow-Headers", "Content-Type, Authorization, If-Match, If-None-Match, "
                // default are: Accept, Accept-Language, Content-Language, Content-Type(subset only)
                + "X-Log-Token, X-Client-Version, X-Client-ID, X-Service-Generation, X-Requested-With");
        headers.add("Access-Control-Expose-Headers", "Location, Retry-After, Content-Encoding, "
                + "ETag, "
                // default exposes are: Cache-Control, Content-Language, Content-type, Expires, Last-Modified, Pragma
                // according to https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Access-Control-Expose-Headers
                + "X-Log-Token, "
                + "X-RateLimit-Limit, X-RateLimit-Limit24h, X-RateLimit-Remaining, X-RateLimit-Reset");
    }
}
