package com.example.service.api;

import java.util.List;

import io.swagger.models.Operation;
import io.swagger.models.parameters.Parameter;

/**
 * Add or complement existing standard headers for Open API documentation completeness
 */
public final class HeaderDocs {

    private HeaderDocs() {
        //intentionally empty
    }

    public static void addStandardParameters(Operation operation) {
        addAcceptContentTypeHeader(operation);
        addLogtokenHeader(operation);
        addServiceGenerationHeader(operation);
        addClientVersionHeader(operation);
    }

    /**
     *
     * The pattern here could be removed as this would open for any type of support in the service and that would be a better
     * way to it in a production system as the distance from this pattern applied to the default headers in this late phase
     * of the open API specification to the code is high and thus this could be removed. I leave it in for now to show how
     * these things could be applied.
     */
    private static void addAcceptContentTypeHeader(Operation operation) {
        String header = "Accept";
        String description = "Default that is set to application/hal+json and that will return the most recent version " +
            "of content. If you want another version use application/hal+json;concept=(the projection);v=(the version)";
        String pattern = "((application\\/hal\\+json)+(, )?(;concept=[a-z][a-z0-9]+)?(;v=[0-9]+)?(, )*)+";
        if (headerDoesNotExist(operation, header)) {
            Parameter accept = new com.example.service.api.HeaderParameter();
            accept.setIn("header");
            accept.setRequired(true);
            accept.setName(header);
            accept.setPattern(pattern);
            accept.setDescription(description);
            operation.addParameter(accept);
        } else {
            complementHeaderInformation(operation, header, description, pattern, true);
        }
    }

    private static void addLogtokenHeader(Operation operation) {
        String header = "X-Log-Token";
        String description = "A Correlation ID that consumers can specify to ensure traceability from own logs." +
            " If not present a token will be generated and returned in response";
        String pattern = "^[a-zA-Z0-9-]{36}$";
        if (headerDoesNotExist(operation, header)) {
            Parameter token = new com.example.service.api.HeaderParameter();
            token.setIn("header");
            token.setRequired(false);
            token.setName(header);
            token.setPattern(pattern);
            token.setDescription(description);
            operation.addParameter(token);
        } else {
            complementHeaderInformation(operation, header, description, pattern, false);
        }
    }

    private static void addServiceGenerationHeader(Operation operation) {
        String header = "X-Service-Generation";
        String description = "A service generation ID that allows consumers to specify an API structure version " +
            "other than the current one. If not present the most recent will be chosen";
        String pattern = "^[0-9]{1}.[0-9]{1}.[0-9]{1}";
        if (headerDoesNotExist(operation, header)) {
            Parameter serviceGeneration = new com.example.service.api.HeaderParameter();
            serviceGeneration.setIn("header");
            serviceGeneration.setRequired(false);
            serviceGeneration.setName(header);
            serviceGeneration.setPattern(pattern);
            serviceGeneration.setDescription(description);
            operation.addParameter(serviceGeneration);
        } else {
            complementHeaderInformation(operation, header, description, pattern, false);
        }
    }

    private static void addClientVersionHeader(Operation operation) {
        String header = "X-Client-Version";
        String description = "A Client version ID is recommended for traceability from own logs.";
        String pattern = "^[0-9]{1}.[0-9]{1}.[0-9]{1}";
        if (headerDoesNotExist(operation, header)) {
            Parameter clientVersion = new com.example.service.api.HeaderParameter();
            clientVersion.setIn("header");
            clientVersion.setRequired(true);
            clientVersion.setName(header);
            clientVersion.setPattern(pattern);
            clientVersion.setDescription(description);
            operation.addParameter(clientVersion);
        } else {
            complementHeaderInformation(operation, header, description, pattern, true);
        }
    }

    private static boolean headerDoesNotExist(Operation operation, String header) {
        List<Parameter> parameters = operation.getParameters();
        boolean addHeader = true;
        if (null != parameters) {
            addHeader = !parameters.stream()
                .filter(param -> param.getName().equals(header))
                .findFirst()
                .isPresent();
        }
        return addHeader;
    }

    private static void complementHeaderInformation(Operation operation, String header, String description,
                                                    String pattern, boolean required) {
        Parameter parameter;
        List<Parameter> parameters = operation.getParameters();
        if (null != parameters) {
            parameter = parameters.stream()
                .filter(param -> param.getName().equals(header))
                .findFirst()
                .get();
            if (null != parameter) {
                if (null == parameter.getDescription() || "".equals(parameter.getDescription())) {
                    parameter.setDescription(description);
                }
                if (null == parameter.getPattern() && pattern != null) {
                    parameter.setPattern(pattern);
                }
                if (!parameter.getRequired() && required) {
                    parameter.setRequired(true);
                }
            }

        }
    }
}
