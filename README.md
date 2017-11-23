# greeting-rest-service

 [![status](https://travis-ci.org/allanhojgaardjensen/breakfastcoding.svg?branch=master)](https://travis-ci.org/allanhojgaardjensen/breakfastcoding) 
 [![coverage](https://codecov.io/gh/allanhojgaardjensen/breakfastcoding/coverage.svg?branch=master)](https://codecov.io/gh/allanhojgaardjensen/breakfastcoding)

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

It is now possible to create a greeting and see that in the greeting list. 
Once you created a greeting by POSTing to /greetings you will see where the greeting
was created in the `201 Created` response containing the `Location` header.

In other words, we see that a response from a POST new greeting returns a 201 created in the event 
of a successful create and it includes a Location header informing the consumer 
about the whereabouts of the newly created greeting.
Furthermore we see that a 400 Bad Request is returned if the request was malformed.

Recreation of the same object is allowed in the current version and thus there is 
no way for the consumer to see if the initial create went well or not, unless the 
consumer does a GET to a location the consumer guesses is the place for the 
newly created greeting and reacts to a not found. This would be a good thing to 
address in a later feature. This may however cause a real problem for consumers that 
are adopting the possibility to recreate/replace using POST. In a real service, that 
would not be recommend to allow for a functionality and then restrict that away 
afterwards.
 
We see that a successfully created greeting is now part of the greetings list.
If stop for a moment and looks at the semantics here, is it ok to have greetings 
posing as separate objects, where language is the differentiator or not.
The code looks different, the objects are no longer Strings they are real objects 
in the implementation, although very simple ones. The objects capable of creating 
resulting json are postfixed Representation. They are not the internal model of a 
service, they are merely representing a given view or projection, that is signaled 
by the content parameter concept={view/projection} and currently that is version 3
 of the content ;v=3 in the endpoint /greetings/{greeting}

We see that the implementation of the initial implementation of
		 /greetings/{greeting} 
for version 1 has become deprecated. As deprecation in the code requires service 
consuming developers to look at the service implementation documentation (javaDoc in this case)
and stating deprecation as a text in a service specification, requires the developers 
to look into the Open API specification and take care of that - or build a compliance 
check into the build pipeline checking for deprecated in the API. So no standardized way 
to inform about that and certainly no dynamic way to inform the consuming application 
about that in real time. We use “X-Status” response header is used for that. 
The observation of `X-Status: deprecated` could be automated into the consumer 
application and a ticket could be raised in their issue backlog in order for them to 
do something about that, or to make the support last a little longer by requesting that 
from the service provider.

Furthermore we are running “2 version overlap” and that means we have 3 versions active right now, 
when we change and deprecate the oldest. You can run any overlapping # of versions, and they do 
have to be the latest versions.
 
If you have very important consumers you may want to keep some of the older versions 
and have them available still, this is somewhat similar to continuing to run old releases. 
You may run them in the same service implementation for a while, with a goal for them to 
be separated or the consumers upgraded. If that is not happening and it becomes a burden 
it is possible to split the service. 

The X-Service-Generation header can be used for that purpose and used for segmentation.

Very simple “just enough” implementations of HAL has been made and the application/hal+json 
is finally appropriate to be used, there are libraries out there that can be used to map 
Representations between Objects and HAL, see HAL information. HAL in the form of 
application/hal+json is an informational standard.


Caution:
Normally you would use the libraries, the “just enough” implementation her is just 
to show the mapping as simple as possible. There are limitations to the implementation 
done here such as not support for array or object, this only supports object. 
This simple implementation is replaced later in the breakfast coding session.

Btw:
Did you have issues handling the “native” object as `native` is a reserved keyword
 in java and thus it needs some form of mapping. This was chosen as a way to 
illustrate that sometimes you will run into thing that are platform implementation 
specifics and there will usually be a way round that.

When you look at the code at this stage you will properly have seen, it is rapidly on 
its way to become a mess. This is entirely my fault, due to a number of bad decisions 
and lack of accuracy in requiring headers being set and correctness in  using HAL etc. 
I created a situation, where the code inside the service get cluttered, despite 
the efforts made to exclude a big portion of the annotations.

So we will do a clean up whilst moving forward - however in a real life setup - 
that would take long time to get away with that having multiple consumers onboard. 


### The Feature "Direct Create new and Replace existing Greetings" - see slides page 68+

The service is still a relative dull service, it is now possible to create a new
greeting, we want to be ready for doing replaces of greetings and possibly create 
new greetings directly under greetings/{new greeting}. The PUT verb is probably the 
best suited for that purpose.

The initial PUT must return a 201 Created with a Location Header, an attempt to 
recreate/replace the greeting must return a 200 OK. The previously implemented 
possibility to recreate using POST must not be possible going forward.

#### Examples

The greeting used underneath is a Danish greeting used in the southern part.

_A consumer preferring English would PUT to greetings/mojn_

````json
{
  "greeting": "Møjn!",
  "language": "Dansk",
  "country": "Danmark",
  "native": {
    "language": "Danish",
    "country": "Denmark"
  },
  "_links": {
    "self": {
      "href": "greetings/mojn",
      "title": "Danish Greeting Mojn"
    }
  }
}
````  

_A consumer preferring Danish would PUT to greetings/mojn_

````json
{
  "greeting": "Møjn!",
  "language": "Dansk",
  "country": "Danmark",
  "native": {
    "language": "Dansk",
    "country": "Danmark"
  },
  "_links": {
    "self": {
      "href": "greetings/mojn",
      "title": "Dansk Hilsen Møjn"
    }
  }
}

````  


## Introduction

A simple rest service that is developed towards being able to greet you in a 
number of languages.

## Worth Noticing

The initial resource "/greetings" is still supported however only using a 
  
    application/json

content-type, where the more semantical correct "/greetings" is supported for
a period of time for compliancy by setting the Accept header to 

    application/hal+json

This is mainly done to show how coexistence thus moving along is possible. 

The resource "/greetings/{greeting}" using the content version scheme using 
"application/hal+json" as producer content-type and showing how to use the 
actual version of content, which is now. 

    application/hal+json;concept=greeting;v=3

if a consumer has the need to go back to a previous version, once a version 2 e.g. 

    application/hal+json;concept=greeting;v=2

Please not that the earliest version has been deprecated here

    application/hal+json;concept=greeting;v=1

If a consumer calls the service and uses _application/hal+json;concept=greeting;v=1_ in the _Accept_ header
the response contains a `X-Status` `deprecated` in order to signal to the consumer that the support for this
version is about to end at some time. The consumer can then make a request through the issue ticketing 
system at the service implementor to get a potential prolonged support for that service.


## Working with the service

Underneath there are a collection of useful things to work with using the 
example.

Useful Commands
---------------
To Build this project:

    mvn verify

To clean an existing checkout and build:

    mvn clean verify

To clean an existing checkout, build and generate API docs:

    mvn clean verify exec:java@api-docs

To run the REST Server standalone:

    mvn exec:java@start-server 

To build, generate site, API docs and run the REST Server standalone:

    mvn clean verify exec:java@api-docs site exec:java@start-server 

To test the REST service use e.g. Postman:
    
    GET http://localhost:8080/greetings

    and get a response 200 OK back, with 
     - Headers 
          content-length: 21
          content-type: application/json
          X-Status : deprecated
     - Body:
            { "greeting": "Hallo!" }

To test the REST service greeting in English:

    GET http://localhost:8080/greetings
    having set Accept-Language "en"

    and get a response 200 OK back, with 
     - Headers 
          content-length: 21
          content-type: application/json
          X-Status : deprecated
     - Body:
            { "greeting": "Hello!" }

To test the REST service use e.g. Postman:
    
    GET http://localhost:8080/greetings/hallo
    having set Accept-Language "da" and
    having set Acccept "application/hal+json" 
        or set to "application/hal+json;concept=greeting" 
        or set to "application/hal+json;concept=greeting;v=3"
    having set X-Log-Token "my-correlation-id"

    and get a response 200 OK back, with 
     - Headers 
            content-length: 184
            content-type: application/hal+json;concept=greeting;v=3
            etag : e5ef5d41
            last-modified: Fri, 15 Sep 2017 18:26:40 GMT (still fixed)
            X-Log-Token: "my-correlation-id"
 
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
                    "self": {
                        "href": "greetings/hallo",
                        "title": "Dansk Hilsen Hallo"
                    }
                }
            }

To test the REST service greeting in English:

    GET http://localhost:8080/greetings/hallo
    having set Accept-Language "da" and
    having set Acccept "application/hal+json" 
        or set to "application/hal+json;concept=greeting" 
        or set to "application/hal+json;concept=greeting;v=3"
    having set X-Log-Token "noget-vi-kender"

    and get a response 200 OK back, with 
     - Headers 
          content-length: 188
          content-type: application/hal+json;concept=greeting;v=3
          etag: cea7c755
          X-Status : deprecated
          last-modified: Fri, 15 Sep 2017 18:26:40 GMT (still fixed)
          X-Log-Token: "noget-vi-kender"
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
                    "self": {
                        "href": "greetings/hallo",
                        "title": "Danish Greeting Hallo"
                    }
                }
            }

To create a greeting

    POST http://localhost:8080/greetings
    having set Accept-Language "en" and
    having set Accept "application/hal+json"
    having set Content-Type "application/json"
    and having request body set to:
            {
              "greeting": "Ohøj!",
              "language": "Dansk",
              "country": "Danmark",
              "native": {
                "language": "Danish",
                "country": "Denmark"
              },
              "_links": {
                "self": {
                  "href": "greetings/ohoj",
                  "title": "Danish Greeting Ohoj"
                }
              }
            }

    and get a response 201 back with

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
            X-Status, 
            X-RateLimit-Limit, 
            X-RateLimit-Limit24h, 
            X-RateLimit-Remaining, 
            X-RateLimit-Reset
        content-length: 0
        location: http://localhost:8080/greetings/ohoj
        X-Log-Token: some UUID 

    - Body:
        (empty)

To get the list of greetings (if you created your own greetings they will be in the list.

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
            X-Status, 
            X-RateLimit-Limit, 
            X-RateLimit-Limit24h, 
            X-RateLimit-Remaining, 
            X-RateLimit-Reset
        content-length: 430
        content-type: application/hal+json;concept=greetings;v=1
        X-Log-Token: some UUID 

    - Body:
            {
                "greetings": {
                    "info": "a list containing current greetings",
                    "_links": {
                        "self": {
                            "href": "/greetings",
                            "type": "application/hal+json;concept=greetinglist;v=1",
                            "title": "List of Greetings"
                        },
                        "greetings": [
                            {
                                "href": "greetings/hallo",
                                "title": "Dansk Hilsen Hallo"
                            },
                            {
                                "href": "greetings/hallo",
                                "title": "Danish Greeting Hallo"
                            },
                            {
                                "href": "greetings/hello",
                                "title": "English Greeting Hello"
                            },
                            {
                                "href": "greetings/hello",
                                "title": "Engelsk Hilsen Hello"
                            }
                        ]
                    }
                }
            }



## The uber-jar

An uber-jar is a so-called "fat jar" containing all the transitive dependencies for the service.
The uber-jar is created during the "package" phase of the build
It is used to facilitate an easy portable complete image of a service.
You can run it as a standalone service.  

To run the uber-jar:

    java -jar target/shaded-greeting-rest-service-1.0-SNAPSHOT.jar
