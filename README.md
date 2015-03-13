To build:

    mvn compile

To run:

    mvn exec:java

To access the service:

    curl -XPOST http://localhost:8080/wordcount -F file=@somefile.zip

The grizzly service listens on localhost:8080. To stop the service cleanly, press Enter in the console where the process was started. The web service creates fils in the default temporary diretory. If the service is not stopped cleanly, some files may be left - they can be identified by the prefix `wordcount`.
