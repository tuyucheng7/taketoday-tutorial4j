#!/bin/bash
java --module-path mods \
     --add-opens tuyucheng.reflected/cn.tuyucheng.taketoday.reflected.internal=tuyucheng.reflecting.named \
     --module tuyucheng.reflecting.named/cn.tuyucheng.taketoday.reflecting.named.Main