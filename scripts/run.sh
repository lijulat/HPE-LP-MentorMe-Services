#!/bin/bash

java -Dserver.port=$PORT $JAVA_OPTS -jar target/mentorme-api.jar
