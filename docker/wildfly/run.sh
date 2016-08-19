#!/bin/sh
docker run --name saatr-wildfly -p 8080:8080 --link saatr-mongo:mongo -d saatr/wildfly
