#!/bin/sh
if docker ps -a | grep --quiet 'saatr-mongo'; then
  docker stop saatr-mongo
  docker rm saatr-mongo
fi
# start mongo (data will be stored on host file system on /var/saatr/datadir)
docker run --name saatr-mongo -v /var/saatr/datadir:/data/db -d mongo:3.3.11
