#!/bin/sh -e

# build saatr application
mvn clean install
cp saatr/target/saatr-*.jar docker/saatr/saatr.jar


# start mongo
. docker/mongo/run.sh

# build and run wildfly with saatr application
cd docker/saatr
sh build.sh
sh run.sh
