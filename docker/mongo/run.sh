#!/bin/sh
docker run --name saatr-mongo -v /var/saatr/datadir:/data/db -d mongo:3.3.11
