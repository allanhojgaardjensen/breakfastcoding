package com.example.hal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * A very simple implementation of the Links objects.
 * Currently only working with a single element "self" and has no ability to be a collection of Link(s).
 */
@ApiModel(value = "_links", description = "the _links object from HAL specification")
public class Links {

    private Link self;

    public Links() {
        // Required by Jackson
    }

    public Links(String href, String title) {
        self =  new Link(href, title);
    }
    @ApiModelProperty(
            access = "public",
            name = "self",
            value = "a reference to the instance itself.")
    public Link getSelf() {
        return self;
    }
}
