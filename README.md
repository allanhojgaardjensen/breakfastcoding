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

This section is about reflecting over the code as it is now, looking at the implemented feature.
Furthermore looking back at the experiences you had during the implementation.
Please comment if you experienced other things during your implementation of the 
"Automate Build and Start optimizing Bandwidth" feature.

We see that a response `200 OK` returned if the consumer has an outdated version. If the consumer includes the 
`If-Modified-Since` and `If-None-Match` a `304 Not Modified` is returned - if the consumer already have the relevant object locally.

The implementation uses a [fixed timestamp](https://github.com/allanhojgaardjensen/breakfastcoding/blob/master/src/main/java/com/example/resource/greeting/Greeting.java#L367) 
(which is unreal - it makes it easier to see the two parts of the optimization, (when demonstrating). The two sides are the temporally based 
_If-Modified-Since_ aspect and the content/state based _If-None-Match_ aspect). 
If the values for the current object is delivered from the client as headers in the request and both are 
the same as the service knows a [`304 Not Modified`](https://github.com/allanhojgaardjensen/breakfastcoding/blob/master/src/main/java/com/example/resource/greeting/Greeting.java#L165) is returned. Otherwise the [modified objects](https://github.com/allanhojgaardjensen/breakfastcoding/blob/master/src/main/java/com/example/resource/greeting/Greeting.java#L167) 
is returned and thus no bandwidth is wasted on retransmitting objects across the wire. In this case the objects are small, but in a real life scenario, that matters. 

Outdated means either content has changed or the the timestamp for the object is 
newer that what the consumer has indicated as last time for the object in question.
A word of caution here is: 
The detection of the last modified and the [entity-tag calculation](https://github.com/allanhojgaardjensen/breakfastcoding/blob/master/src/main/java/com/example/resource/greeting/Greeting.java#L162) should be very 
easy to get, if you have to load an object (possible via an internal model - 
persistency etc.) it may be better just to return the object if small, 
otherwise keep a tuple allowing you to see the key::resource and its (entity-tag, 
last modified) in a map in mem lazy-loaded and tracking a feed on updates to resources.

Furthermore a logtoken (aka correlation id) can be specified from the client side 
using the `X-Log-Token` request header, thus making it easier to communicate with 
the consumer in situations, where it is necessary to look for errors etc in logs 
across domains and consumer infrastructure and service implementor infrastructure.
The service offers the opportunity for a consumer to state an id with the granularity desired from the consumer side. If a log/error self-service is something offered, the consumer developers can easily correlate with what they have and make that as useful for them as they need. 
If unspecified from the consumer side [a token is drawn](https://github.com/allanhojgaardjensen/breakfastcoding/blob/master/src/main/java/com/example/resource/greeting/Greeting.java#L339) and may be used onwards if 
the consuming chooses to use that going forward, otherwise a new token is drawn on 
every request.

On the service build and infrastructure side, reports have been added on source, 
source-doc, coverage, bug and code smells as well as at the “project level”.
Maven site includes checkstyle, PMD, FindBugs and JaCoCo Coverage, Source Code listings, JavDoc and misc. project reportings on dependencies and convergence etc.
There are things is these reports will be optimized before the coding session is over. You find the start link for the reports after you have run the complete build as shown underneath under _Useful Commands_ and open a browser in _target/site_ and point to _index.html_ - the elaborated OpenAPI specification can still be found under _target/api_.

The versions are preserved as the existing consumers are capable to continue, 
on each their current version or move to the newest version.

All consumers using “application/hal+json” for their “Accept” header value are 
moved along with the newest edition of the content for that endpoint in the service. 
Whereas they can individually fall-back to previous version using the  
“application/hal+json;concept=greeting;v={version}” in their Accept header.

### What have we learned sofar?
An initial mess always (I am sorry for that) complicates things, having an unclear 
semantics and improper use of semantics whilst having consumers using services 
will slow us down going forward.
This is a very simple service with an unreal simple implementation, we did not 
really invest a lot in semantics, but the last couple of features brought us the 
ability to move the /greetings/{greeting} resource faster forward, whilst being 
somewhat more stuck with the /greetings resource as the first feature was a 
serious mistake and work needs to be done to get rid of that asap.

 
### The Feature "Create new Greetings" - see slides page 59+

The service is a really dull service, and a fixed set of greetings in English 
and Danish is not going to make that service relevant for anyone for a long time.
Thus we want to be able to create new greetings. In this example service we will 
use the same format for the body of the POSTs although this would not be the case 
in a real service. In a real service the links would not be specified from the 
client the title however would have to be, thus for the sake of simplicity in this 
case the same format was chosen.

#### Examples

The greeting used underneath is a Danish sailor greeting as a tribute to the 
international "Talk like a Pirate Day" which is the 19th of September every year.

_A consumer preferring English would POST to greetings_


````json
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

````  

_A consumer preferring Danish would POST to greetings_

````json
{
  "greeting": "Ohøj!",
  "language": "Dansk",
  "country": "Danmark",
  "native": {
    "language": "Dansk",
    "country": "Danmark"
  },
  "_links": {
    "self": {
      "href": "greetings/ohoj",
      "title": "Dansk Hilsen Ohøj"
    }
  }
}

````  


## Introduction

A simple rest service that is developed towards being able to greet you in a 
number of languages. Currently it will only greet you with a "Hallo" which is
the Danish, Swedish or Norwegian way to say "Hello". Currently it is perceived
as a Danish greeting and the only supported greetings are Danish and English.
We are approaching a version where it is possible to create other greetings.

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


## The uber-jar

An uber-jar is a so-called "fat jar" containing all the transitive dependencies for the service.
The uber-jar is created during the "package" phase of the build
It is used to facilitate an easy portable complete image of a service.
You can run it as a standalone service.  

To run the uber-jar:

    java -jar target/shaded-greeting-rest-service-1.0-SNAPSHOT.jar
