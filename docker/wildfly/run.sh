#!/bin/sh
if docker ps -a | grep --quiet 'saatr-wildfly'; then
  docker stop saatr-wildfly
  docker rm saatr-wildfly
fi
docker run --name saatr-wildfly -p 8080:8080 --link saatr-mongo:mongo -d saatr/wildfly
