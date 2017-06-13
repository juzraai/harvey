#!/bin/bash

echo "Building JAR..."
version=`cat ../.version`
mvn -q clean package \
	&& echo "Running JAR..." \
	&& java -jar target/harvey-app-example-$version-jar-with-dependencies.jar -u root -p root -n test -b example-batch -s 3