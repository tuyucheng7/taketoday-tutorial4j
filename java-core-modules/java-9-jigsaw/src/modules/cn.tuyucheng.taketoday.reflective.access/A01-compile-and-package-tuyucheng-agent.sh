#!/bin/bash

DIR=tuyucheng-agent

# compile
mkdir -p out/${DIR}
javac -d out/${DIR} $(find ${DIR} -type f -name "*.java")

# package
mkdir -p mods
jar --create --file=mods/${DIR}.jar --manifest=${DIR}/manifest.txt -C out/${DIR} .