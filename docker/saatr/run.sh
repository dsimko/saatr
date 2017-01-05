#!/bin/sh
if docker ps -a | grep --quiet 'saatr-app'; then
  docker stop saatr-app
  docker rm saatr-app
fi
docker run --name saatr-app -p 80:8080 -v /var/saatr/logs:/var/saatr/logs --link saatr-mongo:mongo -d saatr/app
