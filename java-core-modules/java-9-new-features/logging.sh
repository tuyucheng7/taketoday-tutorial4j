# compile logging  module
# javac --module-path mods -d mods/cn.tuyucheng.taketoday.logging src/modules/cn.tuyucheng.taketoday.logging/module-info.java src/modules/cn.tuyucheng.taketoday.logging/cn/tuyucheng/taketoday/logging/*.java

# compile logging slf4j module
javac --module-path mods -d mods/cn.tuyucheng.taketoday.logging.slf4j src/modules/cn.tuyucheng.taketoday.logging.slf4j/module-info.java src/modules/cn.tuyucheng.taketoday.logging.slf4j/cn/tuyucheng/taketoday/logging/slf4j/*.java


# compile logging main app module
javac --module-path mods -d mods/cn.tuyucheng.taketoday.logging.app src/modules/cn.tuyucheng.taketoday.logging.app/module-info.java src/modules/cn.tuyucheng.taketoday.logging.app/cn/tuyucheng/taketoday/logging/app/*.java

# run logging main app
# java --module-path mods -m cn.tuyucheng.taketoday.logging.app/cn.tuyucheng.taketoday.logging.app.MainApp

# run looging main app using logback
java --module-path mods -Dlogback.configurationFile=mods/logback.xml -m cn.tuyucheng.taketoday.logging.app/cn.tuyucheng.taketoday.logging.app.MainApp
