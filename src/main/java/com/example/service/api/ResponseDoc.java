package com.example.service.api;

import java.util.Map;

import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Response;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;


/**
 * adds response documentation to an operation in an Open API manner
 */
public class ResponseDoc {
    
    private ResponseDoc() {
        // intentionally empty
    }

    public static void addStandardResponses(Operation operation) {
        addOKResponse200(operation);
        addBadRequestResponse400(operation);
        addNotAuthorizedResponse401(operation);
        addForbiddenResponse403(operation);
        addNotFoundResponse404(operation);
        addNotAcceptableResponse406(operation);
        addConflictResponse409(operation);
        addGoneResponse410(operation);
        addPreconditionFailedResponse412(operation);
        addUnsupportedContentTypeResponse415(operation);
        addClientTooBusyResponse429(operation);
        addServerErrorResponse500(operation);
        addServerBusyResponse503(operation);
        addUnsupportedHTTPVersionResponse505(operation);
    }

    public static void addVerbSpecificHeaders(Path p) {
        if (null != p.getGet()) addGetStandardResponses(p.getGet());
        if (null != p.getPut()) addPutStandardResponses(p.getPut());
        if (null != p.getPost()) addPostStandardResponses(p.getPost());
        if (null != p.getPatch()) addPatchStandardResponses(p.getPatch());
        if (null != p.getDelete()) addDeleteStandardResponses(p.getDelete());
    }

    private static void addGetStandardResponses(Operation getOperation) {
        addAcceptedResponse202(getOperation);
        addNonAuthoritativeInformationResponse203(getOperation);
        addPermanentlyMovedResponse301(getOperation);
        addUnmodifiedResponse304(getOperation);
        addTemporaryRedirectResponse307(getOperation);
        addNotFoundResponse404(getOperation);
        addGoneResponse410(getOperation);
        addNotImplementedResponse501(getOperation);
    }

    private static void addPostStandardResponses(Operation postOperation) {
        addCreatedResponse201(postOperation);
        addAcceptedResponse202(postOperation);
        addPermanentlyMovedResponse301(postOperation);
        addTemporaryRedirectResponse307(postOperation);
        addGoneResponse410(postOperation);
        addPreconditionFailedResponse412(postOperation);
        addUnsupportedContentTypeResponse415(postOperation);
        addClientTooBusyResponse429(postOperation);
        addServerErrorResponse500(postOperation);
        addNotImplementedResponse501(postOperation);
        addServerBusyResponse503(postOperation);
        addUnsupportedHTTPVersionResponse505(postOperation);
    }

    private static void addPutStandardResponses(Operation putOperation) {
        addCreatedResponse201(putOperation);
        addAcceptedResponse202(putOperation);
        addPermanentlyMovedResponse301(putOperation);
        addTemporaryRedirectResponse307(putOperation);
        addGoneResponse410(putOperation);
        addPreconditionFailedResponse412(putOperation);
        addUnsupportedContentTypeResponse415(putOperation);
        addClientTooBusyResponse429(putOperation);
        addServerErrorResponse500(putOperation);
        addNotImplementedResponse501(putOperation);
        addServerBusyResponse503(putOperation);
        addUnsupportedHTTPVersionResponse505(putOperation);
    }

    private static void addDeleteStandardResponses(Operation deleteOperation) {
        addNoContentResponse204(deleteOperation);
    }

    private static void addPatchStandardResponses(Operation patch) {
        //nothing added to the patch verb yet.
    }

    private static void addOKResponse200(Operation operation) {
        String key = "200";
        Map<String, Response> responses = operation.getResponses();
        Response response = new Response();
        if (!responses.containsKey(key)) {
            response.description("OK.");
        } else {
            response = operation.getResponses().get(key);
        }
        addLogTokenResponseHeader(response);
        addUsualResponseHeaders(response);
        operation.addResponse(key, response);
    }

    private static void addCreatedResponse201(Operation operation) {
        String key = "201";
        Map<String, Response> responses = operation.getResponses();
        Response response = new Response();
        if (!responses.containsKey(key)) {
            response.description("Resource Created.");
        } else {
            response = operation.getResponses().get(key);
        }
        addLocationResponseHeader(response);
        addLogTokenResponseHeader(response);
        addUsualResponseHeaders(response);
        operation.addResponse(key, response);
    }

    private static void addAcceptedResponse202(Operation operation) {
        String key = "202";
        Map<String, Response> responses = operation.getResponses();
        Response response = new Response();
        if (!responses.containsKey(key)) {
            response.description("Request accepted for further processing.");
        } else {
            response = operation.getResponses().get(key);
        }
        addLocationResponseHeader(response);
        addRetryAfterResponseHeader(response);
        addLogTokenResponseHeader(response);
        operation.addResponse(key, response);
    }

    private static void addNonAuthoritativeInformationResponse203(Operation operation) {
        String key = "203";
        Map<String, Response> responses = operation.getResponses();
        Response response = new Response();
        if (!responses.containsKey(key)) {
            response.description("Non Authoritative Information");
        } else {
            response = operation.getResponses().get(key);
        }
        addLogTokenResponseHeader(response);
        operation.addResponse(key, response);
    }


    private static void addNoContentResponse204(Operation operation) {
        String key = "204";
        Map<String, Response> responses = operation.getResponses();
        if (!responses.containsKey(key)) {
            Response response = new Response();
            response.description("Request accepted Nothing Returned.");
            addLogTokenResponseHeader(response);
            operation.addResponse(key, response);
        }
    }

    private static void addPermanentlyMovedResponse301(Operation operation) {
        String key = "301";
        Map<String, Response> responses = operation.getResponses();
        Response response = new Response();
        if (!responses.containsKey(key)) {
            response.description("Resource has moved.");
        } else {
            response = operation.getResponses().get(key);
        }
        addLocationResponseHeader(response);
        addLogTokenResponseHeader(response);
        addExpiresHeader(response);
        operation.addResponse(key, response);
    }

    private static void addUnmodifiedResponse304(Operation operation) {
        String key = "304";
        Map<String, Response> responses = operation.getResponses();
        Response response = new Response();
        if (!responses.containsKey(key)) {
            response.description("Not Modified - Resource was not updated");
        } else {
            response = operation.getResponses().get(key);
        }
        addLogTokenResponseHeader(response);
        operation.addResponse(key, response);

    }

    private static void addTemporaryRedirectResponse307(Operation operation) {
        String key = "307";
        Map<String, Response> responses = operation.getResponses();
        Response response = new Response();
        if (!responses.containsKey(key)) {
            response.description("Temporary Redirect - Resource is available shortly else where");
        } else {
            response = operation.getResponses().get(key);
        }
        addLocationResponseHeader(response);
        addLogTokenResponseHeader(response);
        addExpiresHeader(response);
        operation.addResponse(key, response);
    }

    private static void addBadRequestResponse400(Operation operation) {
        String key = "400";
        Response response = new Response();
        Map<String, Response> responses = operation.getResponses();
        if (!responses.containsKey(key)) {
            response.description("Bad Request - the contents of the request were semantically or syntactically wrong.");
        } else {
            response = operation.getResponses().get(key);
        }
        addLogTokenResponseHeader(response);
        operation.addResponse(key, response);
    }

    private static void addNotAuthorizedResponse401(Operation operation) {
        String key = "401";
        Map<String, Response> responses = operation.getResponses();
        Response response = new Response();
        if (!responses.containsKey(key)) {
            response.description("Not Authorized for the resource.");
        } else {
            response = operation.getResponses().get(key);
        }
        addLogTokenResponseHeader(response);
        operation.addResponse(key, response);
    }

    private static void addForbiddenResponse403(Operation operation) {
        String key = "403";
        Map<String, Response> responses = operation.getResponses();
        Response response = new Response();
        if (!responses.containsKey(key)) {
            response.description("Forbidden access to the resource.");
        } else {
            response = operation.getResponses().get(key);
        }
        addLogTokenResponseHeader(response);
        operation.addResponse(key, response);
    }

    private static void addNotFoundResponse404(Operation operation) {
        String key = "404";
        Map<String, Response> responses = operation.getResponses();
        Response response = new Response();
        if (!responses.containsKey(key)) {
            response.description("Resource Not Found");
        } else {
            response = operation.getResponses().get(key);
        }
        addLogTokenResponseHeader(response);
        operation.addResponse(key, response);
    }

    private static void addNotAcceptableResponse406(Operation operation) {
        String key = "406";
        Map<String, Response> responses = operation.getResponses();
        Response response = new Response();
        if (!responses.containsKey(key)) {
            response.description("Not Acceptable - Possible mismatch between headers and content");
        } else {
            response = operation.getResponses().get(key);
        }
        addLogTokenResponseHeader(response);
        operation.addResponse(key, response);
    }

    private static void addConflictResponse409(Operation operation) {
        String key = "409";
        Map<String, Response> responses = operation.getResponses();
        Response response = new Response();
        if (!responses.containsKey(key)) {
            response.description("Conflict - state of resource may have changed.");
        } else {
            response = operation.getResponses().get(key);
        }
        addLogTokenResponseHeader(response);
        operation.addResponse(key, response);
    }

    private static void addGoneResponse410(Operation operation) {
        String key = "410";
        Map<String, Response> responses = operation.getResponses();
        Response response = new Response();
        if (!responses.containsKey(key)) {
            response.description("Gone - resource is no longer available.");
        } else {
            response = operation.getResponses().get(key);
        }
        addLogTokenResponseHeader(response);
        operation.addResponse(key, response);
    }

    private static void addPreconditionFailedResponse412(Operation operation) {
        String key = "412";
        Map<String, Response> responses = operation.getResponses();
        Response response = new Response();
        if (!responses.containsKey(key)) {
            response.description("Precondition Failed - result from state of headers.");
        } else {
            response = operation.getResponses().get(key);
        }
        addLogTokenResponseHeader(response);
        operation.addResponse(key, response);
    }

    private static void addUnsupportedContentTypeResponse415(Operation operation) {
        String key = "415";
        Map<String, Response> responses = operation.getResponses();
        Response response = new Response();
        if (!responses.containsKey(key)) {
            response.description("Content-Type not supported by Resource");
        } else {
            response = operation.getResponses().get(key);
        }
        addLogTokenResponseHeader(response);
        operation.addResponse(key, response);
    }

    private static void addClientTooBusyResponse429(Operation operation) {
        String key = "429";
        Map<String, Response> responses = operation.getResponses();
        Response response = new Response();
        if (!responses.containsKey(key)) {
            response.description("Too much load is added from the client side into the service and the client is requested " +
                "to limit the number of requests - as the limits has been reached");
        } else {
            response = operation.getResponses().get(key);
        }
        addRetryAfterResponseHeader(response);
        addLogTokenResponseHeader(response);
        operation.addResponse(key, response);
    }

    private static void addServerErrorResponse500(Operation operation) {
        String key = "500";
        Map<String, Response> responses = operation.getResponses();
        Response response = new Response();
        if (!responses.containsKey(key)) {
            response.description("The server experienced a currently unknown problem");
        } else {
            response = operation.getResponses().get(key);
        }
        addLogTokenResponseHeader(response);
        operation.addResponse(key, response);
    }

    private static void addNotImplementedResponse501(Operation operation) {
        String key = "501";
        Map<String, Response> responses = operation.getResponses();
        Response response = new Response();
        if (!responses.containsKey(key)) {
            response.description("This method is currently not implemented");
        } else {
            response = operation.getResponses().get(key);
        }
        addLogTokenResponseHeader(response);
        operation.addResponse(key, response);
    }

    private static void addServerBusyResponse503(Operation operation) {
        String key = "503";
        Map<String, Response> responses = operation.getResponses();
        Response response = new Response();
        if (!responses.containsKey(key)) {
            response.description("The service is unavailable");
        } else {
            response = operation.getResponses().get(key);
        }
        addLogTokenResponseHeader(response);
        addRetryAfterResponseHeader(response);
        operation.addResponse(key, response);
    }

    private static void addUnsupportedHTTPVersionResponse505(Operation operation) {
        String key = "505";
        Map<String, Response> responses = operation.getResponses();
        Response response = new Response();
        if (!responses.containsKey(key)) {
            response.description("HTTP Version not supported");
        } else {
            response = operation.getResponses().get(key);
        }
        addLogTokenResponseHeader(response);
        operation.addResponse(key, response);
    }

    private static void addLogTokenResponseHeader(Response response) {
        if (notSet(response, "X-Log-Token")) {
            setHeader(response, "X-Log-Token", "A Correlation ID for consumer use");
        }
    }

    private static void addLocationResponseHeader(Response response) {
        if (notSet(response, "Location")) {
            setHeader(response, "Location", "The Location is used to state where resource can be found");
        }
    }

    private static void addRetryAfterResponseHeader(Response response) {
        if (notSet(response, "Retry-After")) {
            setHeader(response, "Retry-After", "When can the resource be expected at the Location");
        }
    }

    private static void addUsualResponseHeaders(Response response) {
        if (notSet(response, "Content-Type")) {
            setHeader(response, "Content-Type",
                "The concrete content-type returned from service - save on client for future versioning of the particular endpoint");
        }
        if (notSet(response, "Cache-Control")) {
            setHeader(response, "Cache-Control", "The consumer caching information");
        }
        if (notSet(response, "ETag")) {
            setHeader(response, "ETag", "The entity tag");
        }
        addExpiresHeader(response);
        if (notSet(response, "Last-Modified")) {
            setHeader(response, "Last-Modified", "The information was changed at this time");
        }
        if (notSet(response, "Content-Encoding")) {
            setHeader(response, "Content-Encoding", "The concrete content-encoding service");
        }
        addLogTokenResponseHeader(response);
        addRateLimiting(response);
    }

    private static void addExpiresHeader(Response response) {
        if (notSet(response, "Expires")) {
            setHeader(response, "Expires", "The information expiry time");
        }
    }

    private static boolean notSet(Response response, String header) {
        return response.getHeaders() == null || !response.getHeaders().containsKey(header);
    }

    private static void addRateLimiting(Response response) {
        if (!response.getHeaders().containsKey("X-RateLimit-Limit")) {
            setHeader(response, "X-RateLimit-Limit", "X-RateLimit-Limit: Request limit per minute");
        }
        if (!response.getHeaders().containsKey("X-RateLimit-Limit-24h")) {
            setHeader(response, "X-RateLimit-Limit-24h", "X-RateLimit-Limit-24h: Request limit per 24h");
        }
        if (!response.getHeaders().containsKey("X-RateLimit-Remaining")) {
            setHeader(response, "X-RateLimit-Remaining",
                "X-RateLimit-Remaining: Requests left for the domain/resource for the 24h (locally determined)");
        }
        if (!response.getHeaders().containsKey("X-RateLimit-Reset")) {
            setHeader(response, "X-RateLimit-Reset",
                "X-RateLimit-Reset: The remaining window before the rate limit resets in UTC epoch seconds");
        }
    }

    private static void setHeader(Response response, String name, String description) {
        Property contentType = new StringProperty();
        contentType.setName(name);
        contentType.description(description);
        response.addHeader(name, contentType);
    }
    
}
