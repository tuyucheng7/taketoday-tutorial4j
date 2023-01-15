#!/usr/bin/env bash
cd src/main/java
javac cn/tuyucheng/taketoday/java9/aot/JaotCompilation.java
jaotc --output jaotCompilation.so cn/tuyucheng/taketoday/java9/aot/JaotCompilation.class