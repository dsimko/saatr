#!/bin/sh -e
mvn clean install
cp saatr/target/saatr-*.war docker/wildfly/ROOT.war
. docker/mongo/run.sh
cd docker/wildfly
sh build.sh
sh run.sh
