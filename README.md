# Breakfast Code

 [![status](https://travis-ci.org/allanhojgaardjensen/breakfastcoding.svg?branch=master)](https://travis-ci.org/allanhojgaardjensen/breakfastcoding) 
 [![coverage](https://codecov.io/gh/allanhojgaardjensen/breakfastcoding/coverage.svg?branch=master)](https://codecov.io/gh/allanhojgaardjensen/breakfastcoding)
  [![wercker](https://app.wercker.com/status/427a4ef16fd2e665e8ed4eb1462744e3/s/master "wercker status")](https://app.wercker.com/project/byKey/427a4ef16fd2e665e8ed4eb1462744e3)


This is an example of a REST service, which starts in a non-optimal way and then 
during a series of changes evolves and starts being able to handle versions 
and gradually builds it self towards being a better service and serving the 
consumers better. 

The idea is to show how easy things can go wrong and how easy it is just to 
continue in a non-ideal way, this builds up weight and the development speed gets 
slowed down immediately, so this Breakfast Code was about getting away from that and 
move towards a faster development cycle using a number of simple principles. 
In addition to that the session also covers GET, POST, DELETE, PATCH - 
automation of code quality, Open API specification that follows the code and 
useful parts of the HTTP specification in connection with REST.   

## The service itself

    /greetings
                GET  - returns a list greetings
 
    /greetings/{greeting}
                GET - returns a greeting
                POST - creates a new greeting
                PUT - creates a new or replaces a greeting
                DELETE - deletes a greeting
                PATCH - updates a part of a greeting 

### Audience 

The example was developed to a morning session at the Oracle Day in Copenhagen Denmark. The morning session was called "Breakfast Coding" and was done for core developers.

### Tools required

The assumption is that you have Java 8, Maven 3.5 and a git enabled terminal/command prompt available and an editor/IDE.


# Create your own Breakfast Coding Session

Right now you are looking at the end result from the breakfast Coding session.
If you want to go through the session on the basis of what is available here - you can do so.

## Get started with the session
You can use the code in the tagged versions to walk back right to the start of the 
session, which could start 
 * make sure you have `jdk 8` installed - test using `java -version`
 * make sure you have `maven 3.5` installed - test using `mvn -v`
 * make sure you have `git` in your command line - test using `git`

 * create your own repo on github and initialize with a readme.md (a checkmark) and
 * clone that new repo to your development environment and thus have your local edition
 * go into the folder where the cloned repo now is
 
 * download the zip from the link [`v0.1`](https://github.com/allanhojgaardjensen/breakfastcoding/releases/tag/v0.1) to a folder e.g. named e.g. `breakfast-zips` 
 * extract the zip file in that `breakfast-zips` folder and 
 * navigate into the extracted zip files folders and get to `greetings-rest-service-0.1` folder and be at the level where you see the
readme.md file 
 * select all files at that level
 * copy those files into the locally cloned repo

 * build the project using mvn verify and wait for it to download a lot of dependencies 
_(if your are see errors -pls check your proxy options, you repo settings etc)_  

You can download the [slides](
https://github.com/allanhojgaardjensen/breakfastcoding/blob/master/slides/BreakfastCoding.pdf) from github
and find page 23 where the description of the first feature can be found.

## Start developing the first feature

    Now you can implement your own version of the service if you want 
    and you can get the code for each step by either download the zip 
    from the `v0.x` like we did just above for example if you want the 
    solution to the first feature that would be the code found under 
    tag "v0.2"
    
The link to the code for implementation of __feature 1__ "Add English Greeting" [`v0.2`](https://github.com/allanhojgaardjensen/breakfastcoding/releases/tag/v0.2)

    The solution to the second feature can be found under tag "v0.3"

The link to the code for implementation of __feature 2__ "Add Greeting Details" [`v0.3`](https://github.com/allanhojgaardjensen/breakfastcoding/releases/tag/v0.3)
 
and so on....

## Continuing development of features
Each version of the code includes a brief feature descrition for the next upcomming feature and you may use the slides to see the original setup at the actual session.

* The link to the code for implementation of __feature 3__ "Add language details" [`v0.4`](https://github.com/allanhojgaardjensen/breakfastcoding/releases/tag/v0.4)

* The link to the code for implementation of __feature 4__ "Add Basic API Dcoumentation" [`v0.5`](https://github.com/allanhojgaardjensen/breakfastcoding/releases/tag/v0.5)

* The link to the code for implementation of __feature 5__ "Elaborate API Dcoumentation" [`v0.6`](https://github.com/allanhojgaardjensen/breakfastcoding/releases/tag/v0.6)

* The link to the code for implementation of __feature 6__ "Automate and improve code and reporting" [`v0.7`](https://github.com/allanhojgaardjensen/breakfastcoding/releases/tag/v0.7)

* The link to the code for implementation of __feature 7__ "Add ability to Create Greetings" [`v0.8`](https://github.com/allanhojgaardjensen/breakfastcoding/releases/tag/v0.8)

* The link to the code forimplementation of __feature 8__ "Add ability to Create and Replace Greetings" [`v0.9`](https://github.com/allanhojgaardjensen/breakfastcoding/releases/tag/v0.9)

* The link to the code for __feature 9__ "Add ability to Delete Greetings" [`v0.10`](https://github.com/allanhojgaardjensen/breakfastcoding/releases/tag/v0.10)

* The link to the code for __feature 10__ "Add ability to Update parts of Greetings" [`v0.11`](https://github.com/allanhojgaardjensen/breakfastcoding/releases/tag/v0.11)

* The link to the code for _this version_  [`v0.12`](https://github.com/allanhojgaardjensen/breakfastcoding/releases/tag/v0.12)

The idea is that you can go back from the beginning and go through the features as 
you would have done, if you were a part of the Breakfast Coding session. 
Or you can look into the slides or the versions and then move on to having a look 
at the proposed solution in every step.


__A word of caution:__
    
    The code is made for educational purposes and will include ways of doing 
    stuff that solely serves an educational point of view during the different
    steps.

    The latest edition (the one we are looking at right now) is still educational,
    but used the right set of libraries etc. but is still including e.g. use of
    fixed timestamp, a reduced implementation of PATCH, etc.

If you just want to have a look at the different versions, you can also use 
this repo to step back and forth by help of `v0.x` in this repo.


## Continue with the Breakfast Coding

### Findings from the implementation of the previous feature(s)

	This is a section which you can see after each feature is implemented, 
a findings section will be discussion what the findings were. 

    In this findings section it has been about moving towards a more 
    conventional way - that is e.g. getting rid of the locally implemented
    things that could be fetched via dependencies on 3rd party dependencies etc. 

The code now includes 3rd party libraries is is as such ready for the coming features. The homegrown "just enough" HAL libraries are gone and the POM file has new dependencies.


    under "target/site" the maven site report has improved, the site dependency
    convergence has been addressed and is now ok. The service needs to be build
    using e.g. the command `mvn clean verify site` then everything is build.

    under "target/openapi" the open API specification can be found and can be 
    viewed in Swagger Editor or ApiAry.
    
  [ApiAry](https://apiary.io)

  [Swagger Editor](https://editor.swagger.io)
  
  
A sample yaml file has been included in the repo under `/samples/openapi.yml`

_The service includes GET, POST, PUT, DELETE and PATCH, automated code quality setup, automated Open API generation, proper HAL libraries etc. It is my hope that it will bring value to people._

### The Feature "Coming features" 
    This section is also something you can see throughout the session, 
    each version will have a short introduction to the next feature that needs to be implemented. 
    It is possible together with the slides to have some rough idea about what is expected. 
    And you are in a situation, where it is possible to take a glance at the next version as well.
    
The coming features are "Separating the Country from greetings" page 91 in the slides and creating a dedicated country service" page 93 in the slides. The breakfast coding.
The Breakfast Coding session did not include example code for that. This might turn up in the future if the project is maintained and continued. Or you are welcome to add to the latest version of the code here and make it more useful for people.

## Introduction

A simple rest service that is developed towards being able to greet you in a 
number of languages.

## Worth Noticing

This section aims at high-ligthing some of the things that are noteworty.

### The greetings list

    The ressource `/greetings` is supported for
                  application/hal+json and application/json

          which is now the version 
    
                 application/hal+json;concept=greetings;v=2

The previous version is still supported and can be uses as imidiate fall-back for consumers if they set their Accept header to:

    application/hal+json;concept=greetings;v=1

    The resource `/greetings/{greeting}` using the content version scheme using 
                 application/hal+json
         
         as producer content-type and showing how to use the 
         actual version of content, which is now.

                 application/hal+json;concept=greeting;v=4
 
### Default is the newest version

The newest version and the default projection is always returned if the consumer uses Accept:

	application/hal+json

or if the newest version of a particular projection is needed the consumer uses Accept: 

    application/hal+json;concept=greeting
    
In our simple case we only have one projection, but if we had another like sparsegreeting, the consumer could use Accept:

    application/hal+json;concept=sparsegreeting

And get the newest version of that.

### Dynamic backoff to previous version
if a consumer has the need to go back to a previous version, once a version 3 e.g. 

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

To clean an existing checkout, build and generate API docs:

    mvn clean verify

To run the REST Server standalone:

    mvn exec:java@start-server 

To build, generate site, API docs and run the REST Server standalone:

    mvn clean verify site exec:java@start-server 

To run the REST Server standalone (after build):

    mvn exec:java@start-server 


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
         "path":"_links/self/title",
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
    (please note that a If-None-Match is needed on replace)

    DELETE http://localhost:8080/greetings/mojn
    having set Accept-Language "en" and

To directly create a greeting 
    (please note that a If-None-Match is needed on replace)

    
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

To get the list of greetings (if you created your own greetings they will be in the list.

    GET http://localhost:8080/greetings
    having set Accept-Language "en" and
    having set Accept "application/hal+json
    
    and get a response 200 back with

    - Headers:
        Access-Control-Allow-Headers: 
            Content-Type, 
            Authorization, 
            If-Match, 
            If-None-Match, 
            X-Log-Token, 
            X-Client-Version, 
            X-Client-ID, 
            X-Service-Generation, 
            X-Requested-With
        Access-Control-Allow-Methods:
            GET, POST, DELETE, PUT, PATCH, OPTIONS, HEAD
        Access-Control-Allow-Origin: *
        Access-Control-Expose-Headers : 
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
        Cache-Control: no-transform, max-age=30
        Content-Length: 2081
        Content-Type : application/hal+json;concept=greetings;v=2
        ETag:"6dc"
        X-Log-Token: {some UUID} 
    - Body:
            {
                "_links": {
                    "greetings": [
                        {
                            "href": "/greetings/hallo",
                            "templated": true,
                            "type": "application/hal+json;concept=greeting",
                            "title": "Dansk Hilsen Hallo"
                        },
                        {
                            "href": "/greetings/hallo",
                            "templated": true,
                            "type": "application/hal+json;concept=greeting",
                            "title": "Danish Greeting Hallo"
                        },
                        {
                            "href": "/greetings/hello",
                            "templated": true,
                            "type": "application/hal+json;concept=greeting",
                            "title": "English Greeting Hello"
                        },
                        {
                            "href": "/greetings/hello",
                            "templated": true,
                            "type": "application/hal+json;concept=greeting",
                            "title": "Engelsk Hilsen Hello"
                        }
                    ],
                    "self": {
                        "href": "/greetings",
                        "templated": true,
                        "type": "application/hal+json;concept=greetings",
                        "name": "greetingslist",
                        "title": "A list of greetings"
                    }
                },
                "_embedded": {
                    "greetings": [
                        {
                            "_links": {
                                "self": {
                                    "href": "/greetings/hallo",
                                    "templated": false,
                                    "type": "application/hal+json;concept=greeting",
                                    "name": "Danish Greeting Hallo",
                                    "title": "Dansk Hilsen Hallo",
                                    "hreflang": "da",
                                    "seen": "2017-11-24T10:26:48.085Z"
                                }
                            },
                            "greeting": "Hallo!",
                            "language": "Dansk",
                            "country": "Danmark",
                            "native": {
                                "language": "Dansk",
                                "country": "Danmark"
                            }
                        },
                        {
                            "_links": {
                                "self": {
                                    "href": "/greetings/hallo",
                                    "templated": false,
                                    "type": "application/hal+json;concept=greeting",
                                    "name": "Danish Greeting Hallo",
                                    "title": "Danish Greeting Hallo",
                                    "hreflang": "en",
                                    "seen": "2017-11-24T10:26:48.097Z"
                                }
                            },
                            "greeting": "Hallo!",
                            "language": "Dansk",
                            "country": "Danmark",
                            "native": {
                                "language": "Danish",
                                "country": "Denmark"
                            }
                        },
                        {
                            "_links": {
                                "self": {
                                    "href": "/greetings/hello",
                                    "templated": false,
                                    "type": "application/hal+json;concept=greeting",
                                    "name": "English Greeting Hello",
                                    "title": "English Greeting Hello",
                                    "hreflang": "en",
                                    "seen": "2017-11-24T10:26:48.097Z"
                                }
                            },
                            "greeting": "Hello!",
                            "language": "English",
                            "country": "England",
                            "native": {
                                "language": "English",
                                "country": "England"
                            }
                        },
                        {
                            "_links": {
                                "self": {
                                    "href": "/greetings/hello",
                                    "templated": false,
                                    "type": "application/hal+json;concept=greeting",
                                    "name": "English Greeting Hello",
                                    "title": "Engelsk Hilsen Hello",
                                    "hreflang": "da",
                                    "seen": "2017-11-24T10:26:48.097Z"
                                }
                            },
                            "greeting": "Hello!",
                            "language": "English",
                            "country": "England",
                            "native": {
                                "language": "Engelsk",
                                "country": "England"
                            }
                        }
                    ]
                },
                "info": "This is the information v2HAL"
            }
    GET http://localhost:8080/greetings
    having set Accept-Language "en" and
    having set Accept "application/hal+json;concept=greetings;v=1"
    
    and get a response 200 back with

    - Headers:
        Access-Control-Allow-Headers: 
            Content-Type, 
            Authorization, 
            If-Match, 
            If-None-Match, 
            X-Log-Token,
            X-Client-Version, 
            X-Client-ID, 
            X-Service-Generation, 
            X-Requested-With
        Access-Control-Allow-Methods:
            GET, POST, DELETE, PUT, PATCH, OPTIONS, HEAD
        Access-Control-Allow-Origin: *
        Access-Control-Expose-Headers:
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
        Cache-Control: no-transform, max-age=30
        Content-Length: 434
        Content-Type: application/hal+json;concept=greetings;v=1
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
                                "href": "/greetings/hallo",
                                "title": "Dansk Hilsen Hallo"
                            },
                            {
                                "href": "/greetings/hallo",
                                "title": "Danish Greeting Hallo"
                            },
                            {
                                "href": "/greetings/hello",
                                "title": "English Greeting Hello"
                            },
                            {
                                "href": "/greetings/hello",
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
