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
Please comment if you experienced other things during your implementation of the "Add Language Details" feature.

The structure of the service has improved, the different parts has been mad explicit in the code.
The resource is now separate, the service itself and the filter is separated.

Note that the Original getGreetings is still working in order to keep consumers of that version happy. 

The code now contains two versions of the greetings/{greeting} resource. We created a new version, why did we have to do that?
Looking at the requirements given by feature it did stay within open-close (open for extension, close for changes).
So what happened - a string was allowed to be longer than 2 (ISO-2 Country code -> Name of Country) and adding native object.
If you are in full control of consumers you may know that this longer string does not cause a problem, 
if you have a diverse set of consumers it is difficult to know that one of these consumer's UI has a problem coping with information longer than 2 characters for language.
Introducing the new version allows consumers to follow the newest version (using the Accept set to "application/hal+json"), where consumers experiencing
problems with the longer can revert to the previous version (using the Accept set to "application/hal+json;concept=greeting;v=1").

This may prove important as it allows for a service to evolve as fast as needed, without forcing all consumers to upgrade at the same time.

[The endpoint is the same](https://github.com/allanhojgaardjensen/breakfastcoding/blob/master/src/main/java/com/example/resource/greeting/Greeting.java#L96) 
the implementation of [version 1](https://github.com/allanhojgaardjensen/breakfastcoding/blob/master/src/main/java/com/example/resource/greeting/Greeting.java#L112)
 and [version 2](https://github.com/allanhojgaardjensen/breakfastcoding/blob/master/src/main/java/com/example/resource/greeting/Greeting.java#L151) are separated in the the service, however addressed from the one and the same endpoint. 
The setup for greetings/{greeting} is handled by internal routing using the 
[getOrDefault(...)](https://github.com/allanhojgaardjensen/breakfastcoding/blob/master/src/main/java/com/example/resource/greeting/Greeting.java#L100). 
and the supported versions are determined in the 
[construction of the service](https://github.com/allanhojgaardjensen/breakfastcoding/blob/master/src/main/java/com/example/resource/greeting/Greeting.java#L26)
The content based versioning is a minor investment in the future for this service and the initial example of that is seen here.

As you can see from the example tests using postman, you can see that version 2 is now default 
and it is possible for the consumers using version to do an instant "roll-back" to version 1 by 
changing the Accept header to contain "application/hal+json;concept=greeting;v=1"

Why is [getGreetingG1V1](https://github.com/allanhojgaardjensen/breakfastcoding/blob/master/src/main/java/com/example/resource/greeting/Greeting.java#L112) 
which implements version 1 of service endpoint for _greetings/{greeting}_ not deprecated yet? - the reason for that is it is possible to 
work with multiple active versions and thus it is perfectly ok to work with e.g. 2 overlapping versions as here. 

The use of `application/hal+json`at the _greeting/{greeting}_ resource is still wrong from a content-type perspective, 
but not from an `application/json` or `application/hateoas+json` perspective.  
We will change that later to being correct as well as move away from the current way the json is handled in the service implementation.


### The Feature "Add Standard API Documentation" - see slides page 42+

The Service must be documented using openAPI Specification version 2. 
The annotations are used in order to have the code actually defining the API.
When doing API first, the API is usually easier to communication if it has the
right level of abstraction. Therefore the API implemented in code is an extension
of the abstracted API.   

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
