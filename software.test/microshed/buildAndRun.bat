@echo off
call mvn clean package
call docker build -t de.rieckpil.blog/microshed .
call docker rm -f microshed
call docker run -d -p 9080:9080 -p 9443:9443 --name microshed de.rieckpil.blog/microshed