#!/bin/sh
if docker ps -a | grep --quiet 'saatr-app'; then
  docker stop saatr-app
  docker rm saatr-app
fi
docker run --name saatr-app -p 8080:8080 --link saatr-mongo:mongo -d saatr/app
