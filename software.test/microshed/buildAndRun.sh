#!/bin/sh
mvn clean package && docker build -t de.rieckpil.blog/microshed .
docker rm -f microshed || true && docker run -d -p 9080:9080 -p 9443:9443 --name microshed de.rieckpil.blog/microshed