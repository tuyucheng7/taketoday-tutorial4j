#!/usr/bin/env bash
javac --module-path mods:libs -d mods/cn.tuyucheng.taketoday.library.test $(find library-test/src/test -name "*.java")