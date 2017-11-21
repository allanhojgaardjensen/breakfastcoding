# Breakfast Code

This is an example of a REST service which starts in a non-optimal way and then during a series of changes evolves and starts being able to handle versions 
and gradually builds it self towards being a better service and serving the consumers better. The example was developed to a morning session at the Oracle 
Day 2017 in Copenhagen Denmark. The morning session was called "Breakfast Coding"
The assumption is that you have Java 8, Maven 3.5 and a git enabled terminal/command prompt available and an editor/IDE.

## How to use the example

You can use the code to walk through the Breakfast coding walk back and forth using the version tags that are added to the code. The code will contain version 
tags from `v.01` (the initial source) all the way to `v0.12` as the code progresses. 
Each version of the code will contain a request for a feature and that feature is described under the subheading "The Feature"

### The Initial "Feature"
This is the initial source code and thus the only requirement is for you to commit and push the code to a repository of you own choice. 
If you read this online then you want to:

 *  go the `code` section on github in this repository, 
 * chose the `releases` and get the zip code from `v0.1` and store that. 

If you did not create a repository already:

 * create one now and 
 * initiate that with a readme.md file - 
 * clone that repository to your machine.
 
Navigate to the folder where you can see the newly cloned repository readme.md file

Unzip the previously downloaded zip file and step into the folder `breakfastcoding2017-01` and 
 
* select all files from the root of the project including the readme.md file.
* copy all selected files into the local folder containing the initial code.
* check that your code looks like what is in this repository.  

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


