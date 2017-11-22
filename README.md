# Breakfast Code

This is an example of a REST service which starts in a non-optimal way and then during a series of changes evolves and starts being able to handle versions 
and gradually builds it self towards being a better service and serving the consumers better. The example was developed to a morning session at the Oracle 
Day 2017 in Copenhagen Denmark. The morning session was called "Breakfast Coding"
The assumption is that you have Java 8, Maven 3.5 and a git enabled terminal/command prompt available and an editor/IDE.

## How to use the example

You can use the code to walk through the Breakfast coding walk back and forth using the version tags that are added to the code. The code will contain version 
tags from `v.01` (the initial source) all the way to `v0.12` as the code progresses. 
Each version of the code will contain a request for a feature and that feature is described under the subheading "The Feature"
You can download the slides from
https://github.com/allanhojgaardjensen/breakfastcoding/blob/master/slides/BreakfastCoding.pdf
and find page 23 where the description of the first feature can be found.

## Continue with the Breakfast Coding

### Findings from the implementation of the initial feature

This section is about reflecting over the code as it is now, looking at the implemented feature.
Furthermore looking back at the experiences you had during the implementation.
Please comment if you experienced other things during you implementation of the "Add English Greeting" feature.  

Did you break the existing consumer expectations already, by changing the default response from Danish to English (although that would make more sense in a real world example) - Danish was chosen exactly for these reasons and to show how easy it
is to break the "contract".

Did you experience CORS problems?
- perhaps you added a CORS filter like the one below...

```java
    /**
    * a general CORS filter that allows everything from *
    */
    public class CORSFilter implements ContainerResponseFilter {

       public void filter(ContainerRequestContext requestContext, 
                          ContainerResponseContext responseContext) throws IOException {
           MultivaluedMap<String, Object> headers = responseContext.getHeaders();
           headers.add("Access-Control-Allow-Origin", "*");
           headers.add("Access-Control-Allow-Methods", 
                               "GET, POST, DELETE, PUT, PATCH, OPTIONS, HEAD");
           headers.add("Access-Control-Allow-Headers", 
                               "Content-Type, Authorization, If-Match, If-None-Match, "
           // default are: Accept, Accept-Language, Content-Language, Content-Type(subset only)
                   + "X-Log-Token, X-Client-Version, X-Client-ID, X-Service-Generation, X-Requested-With, X-Client-Id");
           headers.add("Access-Control-Expose-Headers", "Location, Retry-After, Content-Encoding, "
                   + "ETag, "
                   // default exposes are: Cache-Control, Content-Language, Content-type, Expires, Last-Modified, Pragma
                   // according to https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Access-Control-Expose-Headers
                   + "X-Log-Token, "
                   + "X-RateLimit-Limit, X-RateLimit-Limit24h, X-RateLimit-Remaining, X-RateLimit-Reset");
       }
    }

```
and registered that in the ServiceExecutor as

```java
    ResourceConfig rc = new ResourceConfig()
                   .packages("com.example")
                   .register(CORSFilter.class);
```
If your did not experience any CORS problems - don't worry just continue, a CORS
filter will be added to the example later. 

The “Accept-Language” header was used for implementation of a users preferred language and the format is specified in 
[RFC2616 section 14.4](https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html)
 
What was your style of implementation “Not Invented Here Syndrome/“aka me” or 
did you use the Local.LanguageRange.parse() or something different.

```java
    String[] languages = preferred.split(",");
    String[] preferredLanguage = Arrays.stream(languages).filter(s ->!s.contains(";")).toArray(String[]::new);
    return preferredLanguage[0];
```

or

```java
    return Locale.LanguageRange.parse(preferred).stream().map(rang‌​e -> new Locale(range.getRange()))
    .collect(Collectors.toList()).get(0).getLanguage();
```

### The Feature "Add Greeting Details" - see slides page 28+

The greeting list actually returns a list of greetings for consumers accepting "application/hal+json" and the greetings themselves are detailed and made an explicit resource.

A concrete greeting must include details on country, e.g. language and country code [ISO2](https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2).

The service endpoint is extended with an explicit greeting, e.g. endpoints

    /greetings/hello
	/greetings/hallo
    
The response must contain text for the UI in language specified in the Accept-Language header, however only supporting Danish and English currently.

#### Examples

The Greetings list
````
Accept-Language header set to "en"  or anything else
An http GET greetings would return

{ "greetings": {
      "info": "a list containing current greetings",
      "_links": {
          "self": {
              "href": "/greetings",
              "type": "application/hal+json;concept=greeetinglist;v=1",
              "title": "List of Greetings"
          },
          "greetings": [{
              "href": "/greetings/hallo",
              "title": "Danish Greeting - Hallo"
              },{
              "href": "/greetings/hello",
              "title": "English Greeting - Hello"
              }]
        }
    }
}

````

The Greeting resource.

````
An http GET greetings/hello having Accept-Language header set to "en" would return
{
    "greeting": "Hello!",
    "country": "GB",
    "_links": {
        "href": "/greetings/hello",
        "title": "English Greeting Hello"
    }
}
````

````
An http GET greetings/hello having Accept-Language header set to "da" would return
{
    "greeting": "Hello!",
    "country": "GB",
    "_links": {
        "href": "/greetings/hello",
        "title": "Engelsk Hilsen Hello"  
    }
}

````

````
An http GET greetings/hallo having Accept-Language header set to "en" would return
{
    "greeting": "Hallo!",
    "country": "DK",
    "_links": {
        "href": "/greetings/hallo",
        "title": "Danish Greeting Hallo"
    }
}
````

````
An http GET greetings/hallo having Accept-Language header set to "da" would return
{
    "greeting": "Hallo!",
    "country": "DK",
    "_links": {
        "href": "/greetings/hallo",
        "title": "Dansk Hilsen Hallo"
    }
}

````

# greeting-rest-service

## Introduction
A simple rest service that is developed towards being able to greet you in a 
number of languages. Currently it will only greet you with a "Hallo" which is
the Danish, Swedish or Norwegian way to say "Hello".


## Working with the service
Underneath there are a collection of useful things to work with using the 
example.

Useful Commands
---------------
To Build this project:

    mvn package

To clean an existing checkout and build:

    mvn clean package


To run the REST Server standalone:

    mvn exec:java 

To test the REST service use e.g. Postman:
    
    GET http://localhost:8080/greetings

    and get a response 200 OK back, with 
     - Headers 
          content-length: 21
          content-type: application/json
     - Body:
            { "greeting": "Hallo!" }

To test the REST service greeting in English:

    GET http://localhost:8080/greetings
    having set Accept-Language "en"

    and get a response 200 OK back, with 
     - Headers 
          content-length: 21
          content-type: application/json
     - Body:
            { "greeting": "Hello!" }

