package com.example.service.api;

/**
 * ensuring that the headers are getting the String type in the Open API documentation
 */
public class HeaderParameter extends io.swagger.models.parameters.HeaderParameter {

    public HeaderParameter() {
        super.type("string");
    }
}
