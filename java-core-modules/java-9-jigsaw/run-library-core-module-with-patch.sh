#!/usr/bin/env bash
java --module-path mods:libs \
--add-modules cn.tuyucheng.taketoday.library.core \
--add-opens cn.tuyucheng.taketoday.library.core/cn.tuyucheng.taketoday.library.core=org.junit.platform.commons \
--add-reads cn.tuyucheng.taketoday.library.core=org.junit.jupiter.api \
--patch-module cn.tuyucheng.taketoday.library.core=outDir/library-test \
--module org.junit.platform.console --select-class cn.tuyucheng.taketoday.library.core.LibraryUnitTest