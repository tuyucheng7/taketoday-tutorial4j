#!/usr/bin/env bash
set -e
docker login -u mogronalol -p $DOCKER_PASSWORD
docker build -t tuyucheng/mesos-marathon-demo:$BUILD_NUMBER .
docker push tuyucheng/mesos-marathon-demo:$BUILD_NUMBER