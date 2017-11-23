package com.example.resource.greeting;

import com.example.hal.Links;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * A Greeting Representation
 */
@ApiModel(value = "Greeting", description = "A greeting representation")
@JsonPropertyOrder({"greeting", "language", "country", "native", "_link"})
public class GreetingRepresentation {

    private String greeting;
    private String language;
    private String country;
    private GreetingNativeRepresentation nativeInfo;
    private Links links;

    public GreetingRepresentation() {
        // default constructor required by Jackson
    }

    public GreetingRepresentation(String greeting, String language, String country) {
        this(greeting, language, country, language, country);
    }

    public GreetingRepresentation(String greeting, String language, String country, String nativeLanguage, String nativeCountry) {
        this(greeting, language, country, nativeLanguage, nativeCountry, greeting.toLowerCase(), greeting);
    }

    public GreetingRepresentation(
            String greeting, String language, String country,
            String nativeLanguage, String nativeCountry,
            String linkHRef, String linkTitle) {
        this.greeting = greeting;
        this.language = language;
        this.country = country;
        this.nativeInfo = new GreetingNativeRepresentation(nativeLanguage, nativeCountry);
        this.links = new Links(linkHRef, linkTitle);
    }

    @ApiModelProperty(
            access = "public",
            name = "greeting",
            example = "Hallo",
            value = "the actual greeting text.")
    public String getGreeting() {
        return greeting;
    }

    @ApiModelProperty(
            access = "public",
            name = "language",
            example = "English",
            value = "the language spoken for people using the actual greeting.")
    public String getLanguage() {
        return language;
    }

    @ApiModelProperty(
            access = "public",
            name = "country",
            example = "England",
            value = "the country where the actual greeting is used.")
    public String getCountry() {
        return country;
    }

    @JsonProperty("native")
    public GreetingNativeRepresentation getNative() {
        return nativeInfo;
    }

    @JsonProperty("native")
    public void setGreetingRepresentation(GreetingNativeRepresentation nativeInfo) {
        this.nativeInfo = nativeInfo;
    }

    @JsonProperty("_links")
    public Links getLinks() {
        return links;
    }

    @JsonProperty("_links")
    public void setLinks(Links links) {
        this.links = links;
    }

    public String toHAL() {
        return "{"
                + "\"greeting\":\"" + greeting + "\","
                + "\"language\":\"" + language + "\","
                + "\"country\":\"" + country + "\","
                + "\"native\":{"
                + "\"language\":\"" + nativeInfo.getLanguage() + "\","
                + "\"country\":\"" + nativeInfo.getCountry() + "\""
                + "},"
                + "\"_links\":{"
                + "\"self\":{"
                + "\"href\":\"" + links.getSelf().getHref() + "\","
                + "\"title\":\"" + links.getSelf().getTitle() + "\""
                + "}"
                + "}"
                + "}";
    }
}
