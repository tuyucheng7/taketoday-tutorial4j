#!/usr/bin/env bash
java --module-path mods:libs \
--add-modules cn.tuyucheng.taketoday.library.test \
--add-opens cn.tuyucheng.taketoday.library.core/cn.tuyucheng.taketoday.library.core=cn.tuyucheng.taketoday.library.test \
org.junit.platform.console.ConsoleLauncher --select-class cn.tuyucheng.taketoday.library.test.LibraryUnitTest