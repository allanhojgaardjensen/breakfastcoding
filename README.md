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
Please comment if you experienced other things during your implementation of the "Add API Documentation" feature.

The structure of the service has improved, the different parts has been mad explicit in the code.
A package for the rudimentary API doc generation has been added.     

The Open API specification in this initial edition can be found in the target/api folder.
The YAML file can be opened in editor.swagger.io and apiary.io and the API viewed there.

Annotations like e.g. ApiOperation etc. has been added to the code.
A service API specification was generated as a part of the build.
The documentation can now be viewed in the swagger editor and apiary, they both support the OpenAPI. 


The API will have a version that follows the code and could be deployed as a part of an service catalogue 
having a service catalogue client that can view the code, deliver endpoints for “try it”, and links to 
subscriptions, contracts  etc. That application is promoted using every normal means for promoting 
applications such as SEO to make your service rank well, when potential consumers are searching for services 
they can build their business on top off.

If you have done the implementation using swagger annotations directly in the code, 
you may have used quite some space in the Greeting resource and that may clutter 
the development experience.
The development speed of the service is important and the complexity should not 
be within the documentation of the service, that should be with the services and 
the ability to allow for individual consumers to catch up on service content 
versions in their own pace. 

Note that the Original getGreetings is still working in order to keep consumers of that version happy. 
Deprecation is now moved from code to API docs that this version of the endpoint will be terminated at some point in time.

The use of `application/hal+json`at the _greeting/{greeting}_ resource is still wrong from a content-type perspective, 
but not from an `application/json` or `application/hateoas+json` perspective.  
We will change that later to being correct as well as move away from the current way the json is handled in the service implementation.

### The Feature "Automate build and elaborate API Documentation" - see slides page 47+

The greeting service now have some basic documentation, that can be viewed in tools 
understanding OpenAPI specification version 2. There are some parts missing though. 
There is no expectations defined in the API that states a consumer must be able to 
handle a bad request, non-supported content-type, permanently moved etc. 
Specifying that using annotations would further clutter the code.
Service must have elaborated API documentation.

The feature targets the having an easy to read documentation of the API and 
prepare consumers for changes to the API, resources may be moved, 
operations may be deferred, content may not be authoritative from a given 
endpoint.


The greeting list continues to return a list of greetings for consumers accepting "application/hal+json" and the greetings themselves remains detailed as explicit resources.


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

To clean an existing checkout, build and generate API docs:

    mvn clean package exec:java@api-docs

To run the REST Server standalone:

    mvn exec:java@start-server 

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
    having set Accept-Language "en" and
    having set Accept "application/hal+json"    
    and get a response 200 back with

    - Headers:
        access-control-allow-headers: 
            Content-Type, 
            Authorization, 
            If-Match, 
            If-None-Match, 
            X-Log-Token, 
            X-Client-Version, 
            X-Client-ID, 
            X-Service-Generation, 
            X-Requested-With

        access-control-allow-methods: GET, POST, DELETE, PUT, PATCH, OPTIONS, HEAD
        access-control-allow-origin: *
        access-control-expose-headers: 
            Location, 
            Retry-After,    
            Content-Encoding, 
            ETag, 
            X-Log-Token, 
            X-RateLimit-Limit, 
            X-RateLimit-Limit24h, 
            X-RateLimit-Remaining, 
            X-RateLimit-Reset
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

    GET greetings/hello 
    having set Accept-Language "en" and Accept "application/hal+json" or "application/hal+json;concept=greeting;v=2" or "application/hal+json;concept=greeting"
    and get a response 200 OK back, with 
     - Headers 
          content-length: 223
          content-type: application/hal+json;concept=greeting;v=2
     - Body:
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

To test the REST service greetings `hello` resource in Danish:

    GET greetings/hello 
    having set Accept-Language "da" and Accept "application/hal+json" or "application/hal+json;concept=greeting;v=2" or "application/hal+json;concept=greeting"
    and get a response 200 OK back, with 
     - Headers 
          content-length: 221
          content-type: application/hal+json;concept=greeting;v=2
     - Body:
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


To test the REST service greetings `hallo` resource in English:

    GET greetings/hallo 
    having set Accept-Language "en" and Accept "application/hal+json" or "application/hal+json;concept=greeting;v=2" or "application/hal+json;concept=greeting"
    and get a response 200 OK back, with 
     - Headers 
          content-length: 219
          content-type: application/hal+json;concept=greeting;v=2
     - Body:
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

To test the REST service greetings `hallo` resource in Danish:

    GET greetings/hallo having set Accept-Language "da" and Accept "application/hal+json" or "application/hal+json;concept=greeting;v=2" or "application/hal+json;concept=greeting"
    and get a response 200 OK back, with 
     - Headers 
          content-length: 215
          content-type: application/hal+json;concept=greeting;v=2
     - Body:
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


To test the REST service greetings `hello` resource in English from previous version in same endpoint:

    GET http://localhost:8080/greetings/hello
    having set Accept-Language "en" and Accept "application/hal+json;concept=greeting;v=1"

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

To test the REST service greetings `hello` resource in Danish from previous version in same endpoint:

    GET http://localhost:8080/greetings/hello
    having set Accept-Language "da" and Accept "application/hal+json;concept=greeting;v=1"

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

To test the REST service greetings `hallo` resource in English from previous version in same endpoint:

    GET http://localhost:8080/greetings/hallo
    having set Accept-Language "en" and Accept "application/hal+json;concept=greeting;v=1"

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

To test the REST service greetings `hallo` resource in Danish from previous version in same endpoint:

    GET http://localhost:8080/greetings/hallo
    having set Accept-Language "da" and Accept "application/hal+json;concept=greeting;v=1"

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

## The uber-jar

An uber-jar is a so-called "fat jar" containing all the transitive dependencies for the service.
The uber-jar is created during the "package" phase of the build
It is used to facilitate an easy portable complete image of a service.
You can run it as a standalone service.  

To run the uber-jar:

    java -jar target/shaded-greeting-rest-service-1.0-SNAPSHOT.jar
