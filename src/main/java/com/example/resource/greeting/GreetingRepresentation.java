package com.example.resource.greeting;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.openapitools.jackson.dataformat.hal.HALLink;
import io.openapitools.jackson.dataformat.hal.annotation.Link;
import io.openapitools.jackson.dataformat.hal.annotation.Resource;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * A Greeting Representation
 */
@Resource
@ApiModel(value = "Greeting", description = "A greeting representation")
public class GreetingRepresentation {

    private String greeting;
    private String language;
    private String country;
    private GreetingNativeRepresentation nativeInfo;

    @Link
    private HALLink self;

    public GreetingRepresentation() {
        // default constructor required by Jackson
    }

    public GreetingRepresentation(GreetingRepresentation gr) {
        this.greeting = gr.getGreeting();
        this.country = gr.getCountry();
        this.language = gr.getLanguage();
        this.nativeInfo = gr.getNative();
        this.self = gr.getSelf();
    }

    public GreetingRepresentation(String greeting, String language, String country, GreetingNativeRepresentation nativeInfo, HALLink self) {
        this.greeting = greeting;
        this.language = language;
        this.country = country;
        this.nativeInfo = nativeInfo;
        this.self = self;
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
    public void setNativeRepresentation(GreetingNativeRepresentation nativeInfo) {
        this.nativeInfo = nativeInfo;
    }

    public HALLink getSelf() {
        return self;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.greeting);
        hash = 19 * hash + Objects.hashCode(this.language);
        hash = 19 * hash + Objects.hashCode(this.country);
        hash = 19 * hash + Objects.hashCode(this.nativeInfo);
        hash = 19 * hash + Objects.hashCode(this.self);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final GreetingRepresentation other = (GreetingRepresentation) obj;
        if (!Objects.equals(this.greeting, other.greeting))
            return false;
        if (!Objects.equals(this.language, other.language))
            return false;
        if (!Objects.equals(this.country, other.country))
            return false;
        if (!Objects.equals(this.nativeInfo, other.nativeInfo))
            return false;
        if (!Objects.equals(this.self, other.self))
            return false;
        return true;
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
                + "\"href\":\"" + self.getHref() + "\","
                + "\"title\":\"" + self.getTitle() + "\""
                + "}"
                + "}"
                + "}";
    }
    
    public String toHATEOAS() {
        return "{"
                + "\"greeting\":\"" + greeting + "\","
                + "\"language\":\"" + language + "\","
                + "\"country\":\"" + country + "\","
                + "\"native\":{"
                + "\"language\":\"" + nativeInfo.getLanguage() + "\","
                + "\"country\":\"" + nativeInfo.getCountry() + "\""
                + "},"
                + "\"_links\":{"
                + "\"href\":\"" + self.getHref() + "\","
                + "\"title\":\"" + self.getTitle() + "\""
                + "}"
                + "}";
    }

    public String toString() {
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
                + "\"href\":\"" + self.getHref() + "\","
                + "\"title\":\"" + self.getTitle() + "\""
                + "}"
                + "}"
                + "}";
    }
}
