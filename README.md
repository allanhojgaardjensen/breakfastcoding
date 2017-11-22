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

### Findings from the implementation of the previous feature

This section is about reflecting over the code as it is now, looking at the implemented feature.
Furthermore looking back at the experiences you had during the implementation.
Please comment if you experienced other things during you implementation of the "Add Greeting Details" feature.

The CORS filter was added to the example as well as the build now includes assembly of an uber-jar, which can be used for packaging the service into docker or just run as a jar file without further overhead.

Note that the Original getGreetings was deprecated, but is still working in order to keep consumers of that version happy. Deprecation signals that this version of the endpoint will be terminated at some point in time.

The code now contains a [versionable edition](https://github.com/allanhojgaardjensen/breakfastcoding/blob/master/src/main/java/com/example/Greeting.java#L202) for greetings/{greeting}. The versioning uses content based versioning this is a minor investment in the future for this service, handling of unsupported media-types using [getOrDefault(...)](https://github.com/allanhojgaardjensen/breakfastcoding/blob/master/src/main/java/com/example/Greeting.java#L99). Supported versions are determined in the [construction of the service](https://github.com/allanhojgaardjensen/breakfastcoding/blob/master/src/main/java/com/example/Greeting.java#L26)

Btw. what do you think about the use `application/json` for the resources in the current implementation of 

    /greetings 
                            
	/greetings/{greeting}

Where is it appropriate to use `application/hateoas+json` and where would `application/hal+json` be a better fit?

It seems that `application/json` would be useful to _greetings_ and _greetings/{greeting}_, whereas `application/hateoas+json` would be useful and correct for the list containing greetings.

The `application/json`is not problematic from a content point of view for the _greetings_ resource, but it seems to be from a semantic point of view, the semantics of that endpoint are dubious. 

The use of `application/hal+json`at the _greeting/{greeting}_ resource is wrong from a content-type perspective, but not from an `application/json` or `application/hateoas+json` perspective.  


### The Feature "Add Language Details" - see slides page 35+

The greeting list continues to return a list of greetings for consumers accepting "application/hal+json" and the greetings themselves remains detailed as explicit resources.

A concrete greeting must include the name of the country. Furthermore it needs to include a native part for the consumer, which states the language and country in consumers own language.

The service endpoint is greetings/{greeting} as the endpoints shown below

    /greetings/hello
    /greetings/hallo
    
The response must contain text for the UI in language specified in the Accept-Language header as well as the native part on country and language, however only supporting Danish and English currently.

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
    "language": "English",
    "country": "England",
    "native": {
        "language": "English",
        "country": "England"
    },
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
    "language": "English",
    "country": "England",
    "native": {
        "language": "Engelsk",
        "country": "England"
    },
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
    "language": "Dansk",
    "country": "Danmark",
    "native": {
        "language": "Danish",
        "country": "Denmark"
    },
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
    "language": "Dansk",
    "country": "Danmark",
    "native": {
        "language": "Dansk",
        "country": "Danmark"
    },
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
the Danish, Swedish or Norwegian way to say "Hello". Currently it is perceived
as a Danish greeting and the only supported greetings are Danish and English.

## Worth Noticing

The initial resource "/greetings" is now only supported using a 
  
    application/json

content-type, where the more semantical correct "/greetings" is supported for
a period of time for compliancy by setting the Accept header to 

    application/hal+json

This is mainly done to show how coexistence thus moving along is possible. 
Furthermore the resource "/greetings/{greeting}" is now made with the content
version scheme using "application/hal+json" as producer content-type and 
showing how to use the actual version of content. 

    application/hal+json;concept=greeting;v=1

if a consumer has the need to go back to that version, once a version 2 e.g. 

    application/hal+json;concept=greeting;v=2

is created in a near future.

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
    having set Accept "application/json"
    and get a response 200 OK back, with 
     - Headers 
          content-length: 21
          content-type: application/json
     - Body:
            { "greeting": "Hallo!" }

To test the REST service greeting in English:

    GET http://localhost:8080/greetings
    having set Accept-Language "en" and Accept "application/json"

    and get a response 200 OK back, with 
     - Headers 
          content-length: 21
          content-type: application/json
     - Body:
            { "greeting": "Hello!" }

To test the REST service greeting List:

    GET http://localhost:8080/greetings
    having set Accept "application/hal+json"

    and get a response 200 OK back, with 
     - Headers 
          content-length: 458
          content-type: application/hal+json
     - Body:
        {
            "greetings": {
                "info": "a list containing current greetings",
                "_links": {
                    "self": {
                        "href": "/greetings",
                        "type": "application/hal+json;concept=greeetinglist;v=1",
                        "title": "List of Greetings"
                    },
                    "greetings": [
                        {
                            "href": "/greetings/hallo",
                            "title": "Danish Greeting - Hallo"
                        },
                        {
                            "href": "/greetings/hello",
                            "title": "English Greeting - Hello"
                        }
                    ]
                }
            }
        }

To test the REST service greetings `hello` resource in English:

    GET http://localhost:8080/greetings/hello
    having set Accept-Language "en" and Accept "application/hal+json"

    and get a response 200 OK back, with 
     - Headers 
          content-length: 127
          content-type: application/hal+json;concept=greeting;v=1
     - Body:
        {
            "greeting": "Hello!",
            "country": "GB",
            "_links": {
                "href": "/greetings/hello",
                "title": "English Greeting Hallo"
            }
        }

To test the REST service greetings `hello` resource in Danish:

    GET http://localhost:8080/greetings/hello
    having set Accept-Language "da" and Accept "application/hal+json"

    and get a response 200 OK back, with 
     - Headers 
          content-length: 125
          content-type: application/hal+json;concept=greeting;v=1
     - Body:
        {
            "greeting": "Hello!",
            "country": "GB",
            "_links": {
                "href": "/greetings/hello",
                "title": "Engelsk Hilsen Hello"
            }
        }

To test the REST service greetings `hallo` resource in English:

    GET http://localhost:8080/greetings/hallo
    having set Accept-Language "en" and Accept "application/hal+json"

    and get a response 200 OK back, with 
     - Headers 
          content-length: 126
          content-type: application/hal+json;concept=greeting;v=1
     - Body:
        {
            "greeting": "Hallo!",
            "country": "DK",
            "_links": {
                "href": "/greetings/hallo",
                "title": "Danish Greeting Hallo"
            }
        }

To test the REST service greetings `hallo` resource in Danish:

    GET http://localhost:8080/greetings/hallo
    having set Accept-Language "da" and Accept "application/hal+json"

    and get a response 200 OK back, with 
     - Headers 
          content-length: 123
          content-type: application/hal+json;concept=greeting;v=1
     - Body:
        {
            "greeting": "Hallo!",
            "country": "DK",
            "_links": {
                "href": "/greetings/hallo",
                "title": "Dansk Hilsen Hallo"
            }
		}

## Packaging an uber-jar

A fat jar containing all dependencies is created during the package phase of the build. 

To run the uber-jar:

    java -jar target/shaded-greeting-rest-service-1.0-SNAPSHOT.jar
    
    