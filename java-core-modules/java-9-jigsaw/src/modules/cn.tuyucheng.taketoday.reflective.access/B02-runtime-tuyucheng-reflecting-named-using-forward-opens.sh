#!/bin/bash
java --module-path mods \
     --add-opens tuyucheng.reflected/cn.tuyucheng.taketoday.reflected.internal=tuyucheng.intermedium \
     --module tuyucheng.reflecting.named/cn.tuyucheng.taketoday.reflecting.named.MainWithForwardOpen