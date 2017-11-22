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

The Open API specification in this more elaborate edition can be found in the target/api folder.
The YAML file can be opened in editor.swagger.io and apiary.io and the API viewed there.

Annotations like e.g. ApiOperation etc. is still in the code, and the extended service API specification
is generated as a part of the build. If you view the generated specification in swagger editor and apiary,
you will see a more detailed OpenAPI specification now. 

As mentioned previously, the development speed of the service is important and the complexity should not 
be within the documentation of the service, that should be with the services and the ability to allow for 
individual consumers to catch up on service content versions in their own pace. There is more to it than 
versioning, the consumers needs to be aware of what they can expect from the service. 
Thus in order to create the best situation for the service itself, it is necessary to prepare 
the consumers for the responses that might occur. If a service does not specify a 301, the 
consumer may not prepare for that response and that will be perceived as an error.
Even the 301 is standard part of the HTTP specification, the perception of error is the result.

The implementation of the automated and elaborated API documentation and there are a number 
of responses that are relevant to signal to a consumer, using the OpenAPI specification, 
in order to prepare the consumer  for 301, 202, 415, .... responses etc. and 
specifying which headers accompanies these responses and what do they mean. 

Why not just include all possible responses not included in the generated specification and make
the consumers prepare for everything? This would not really be service oriented and there needs 
to be a balance for what you anticipate is a realistic response you are going to use either now or
in a future not too far away from now. 
Everyone has to figure out what their needs are in relation to responses and headers for an 
endpoint or service, and what could the general set you would include as a function of e.g. verb as done here.
The important part is that it is possible to signal to consumers what they can expect 
and what responses they need to be able to react to.

Whether this example is including too much or not is definitely a topic worth discussing.
I have included a set of predefined request headers together with a set of general 
responses and verb specific responses. If these had to be included in the code, 
that would have cluttered the code significantly.
 
### The Feature "Automate build and Start Optimizing Bandwidth" - see slides page 51+

The greeting service now have some elaborated documentation, that can be viewed in tools 
understanding OpenAPI specification version 2. The reality is that the service does not 
use any form of optimization e.g. not returning content to the consumers, when they 
already have the newest version.  

The feature targets having the ability to only return e.g. currently we may cache 
for a long time as no new instances of greetings can be made yet, and thus we will 
start by delivering a [ETag](https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.19) 
value back that the client can present to the service endpoint and thus the service endpoint 
implementation can know whether to return a version to the consumer or signal back that the 
consumer already has the correct version.


The greeting list continues to return a list of greetings for consumers accepting "application/hal+json" and the greetings themselves remains detailed as explicit resources.

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
