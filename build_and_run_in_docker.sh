#!/bin/sh -e

# build saatr application
mvn clean install
cp saatr/target/saatr-*.war docker/wildfly/ROOT.war

# start mongo
. docker/mongo/run.sh

# build and run wildfly with saatr application
cd docker/wildfly
sh build.sh
sh run.sh
