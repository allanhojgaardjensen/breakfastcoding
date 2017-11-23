package com.example.resource.greeting;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Includes the native part of a greeting.
 * The native part includes the language spoken and the country.
 */
@ApiModel(value = "native", description = "the probably prefered language of the consumer")
public class GreetingNativeRepresentation {
    
    private String language;
    private String country;

    public GreetingNativeRepresentation() {
        // required by Jackson
    }

    public GreetingNativeRepresentation(String language, String country) {
        this.language = language;
        this.country = country;
    }

    @ApiModelProperty(
            access = "public",
            name = "language",
            example = "English",
            value = "the preferred language for consumer of the actual greeting.")
    public String getLanguage() {
        return language;
    }

    @ApiModelProperty(
            access = "public",
            name = "country",
            example = "English",
            value = "the country from which the language of the consumer origins.")
    public String getCountry() {
        return country;
    }
}

