#!/bin/bash

mvn -B -DskipTests clean dependency:list install
# mvn clean package -Dmaven.test.skip=true
