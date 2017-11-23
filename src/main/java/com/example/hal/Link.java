package com.example.hal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * A very simple implementation of a HAL Link Object
 */
@ApiModel(value = "Link", description = "Very simple HAL Link")
public class Link {
    
    private String href;
    private String title;

    public Link() {
        // Required by Jackson
    }

    public Link(String href, String title) {
        this.href = href;
        this.title = title;
    }

    @ApiModelProperty(
            access = "public",
            name = "href",
            example = "/greetings/hello",
            value = "a reference to an instance of a greeting.")
    public String getHref() {
        return href;
    }

    @ApiModelProperty(
            access = "public",
            name = "title",
            example = "An English Greeting Hello",
            value = "a title for the greeting.")
    public String getTitle() {
        return title;
    }
}

