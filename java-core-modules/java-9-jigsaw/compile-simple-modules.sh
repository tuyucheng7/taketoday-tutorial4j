#!/usr/bin/env bash
javac -d mods --module-source-path src/simple-modules $(find src/simple-modules -name "*.java")