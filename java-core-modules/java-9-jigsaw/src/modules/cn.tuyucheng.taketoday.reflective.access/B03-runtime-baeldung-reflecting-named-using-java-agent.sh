#!/bin/bash
java --module-path mods \
     -javaagent:mods/tuyucheng-agent.jar=cn.tuyucheng.taketoday.reflected.internal.InternalNonPublicClass,cn.tuyucheng.taketoday.reflecting.named.Main \
     --module tuyucheng.reflecting.named/cn.tuyucheng.taketoday.reflecting.named.Main
