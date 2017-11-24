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

The service is a little cleaner as the earlier greetings was taken out of support.

We see that it is possible to partially update an object and get a 200 ok 
response as well as the 400 and 409 from a PATCH verb. 
The current implementation is a very simple edition of parts of the RFC6902 only 
supporting  replace and doing that for non-array objects. PATCH is usually only 
used when objects are humongous and not for objects of this size. There is still 
quite a lot of discussion around the actual path PATCH implementations should take, 
I have used the content-type ”application/patch+json” and not 
”application/json-patch+json” as this is not strictly following or complete the 
RFC. PATCH is not idempotent and safe as we saw for e.g. PUT.

Concerning the implementation, I chose to return 200 and return a status, 
instead of 204 No Content, the return code 422 is not included in the implementation
as I did not think such a situation could be included in a decent way in the current
implementation problem domain. The use of "application/patch+json" is used to signal
clearly that this is a very limited implementation only supporting the replace operation
from PATCH.

It is my hope that it will bring value anyway.


### The Feature "Maturing for the coming features " 
The coming features are "Separating the Country from greetings" page 91 in the slides
And creating a dedicated country service" page 93 in the slides. The breakfast coding
session did not include example code for that. This might turn up in the future if
the project is maintained and continued. Or you are welcome to add to the latest version
of the code here and make it more useful for people.

Partially updating an object could be done using PATCH /greetings/{greeting}, e.g.

 PATCH /greetings/mojn
 Accept-Language "en"

````json
    {
     "op":"replace",
     "path":"links/self/title",
     "value":"This is the new title"
    }
````
 PATCH /greetings/mojn
 Accept-Language "en"

````json
    {
     "op":"replace",
     "path":"language",
     "value":"Land"
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

Please not that the earliest version has been removed 

    application/hal+json;concept=greeting;v=1

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
    having set Accept-Language "en" and
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

To directly patch a greeting

    PATCH http://localhost:8080/greetings/mojn
    having set Accept-Language "en" and
    having set Accept "application/json"
    having set Accept-Patch "application/patch+json"
    having set If-None-Match "{correct hash}"
    having set Content-Type "application/patch+json"
    and request body set to:
        {
         "op":"replace",
         "path":"language",
         "value":"Patched Language"
        }
 
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
        content-length: 30
        location: http://localhost:8080/greetings/mojn
        X-Log-Token: {some UUID}

    - Body:
        {"status":"value is replaced"}



    PATCH http://localhost:8080/greetings/mojn
    having set Accept-Language "en" and
    having set Accept "application/json"
    having set Accept-Patch "application/patch+json"
    having set If-None-Match "{correct hash}"
    having set Content-Type "application/patch+json"
    and request body set to:
        {
         "op":"replace",
         "path":"links/self/title",
         "value":"Patched Title"
        }

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
        content-length: 30
        content-type: 
        location: http://localhost:8080/greetings/mojn
        X-Log-Token: {some UUID}

    - Body:
        {"status":"value is replaced"}

To directly delete a greeting

    DELETE http://localhost:8080/greetings/mojn
    having set Accept-Language "en" and

To directly create a greeting

    PUT http://localhost:8080/greetings/mojn
    having set Accept-Language "en" and
    having set Accept "application/hal+json"
    having set Content-Type "application/json"
    and having request body set to:
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
        location: http://localhost:8080/greetings/mojn
        X-Log-Token: {some UUID}

    - Body:
        (empty)

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
        X-Log-Token: {some UUID} 

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
        X-Log-Token: {some UUID} 

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
