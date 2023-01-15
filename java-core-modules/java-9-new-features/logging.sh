# 编译logging模块
javac --module-path mods -d mods/cn.tuyucheng.taketoday.logging src/modules/cn.tuyucheng.taketoday.logging/module-info.java src/modules/cn.tuyucheng.taketoday.logging/cn/tuyucheng/taketoday/logging/*.java

# 编译logging slf4j模块
javac --module-path mods -d mods/cn.tuyucheng.taketoday.logging.slf4j src/modules/cn.tuyucheng.taketoday.logging.slf4j/module-info.java src/modules/cn.tuyucheng.taketoday.logging.slf4j/cn/tuyucheng/taketoday/logging/slf4j/*.java

# 编译logging app模块
javac --module-path mods -d mods/cn.tuyucheng.taketoday.logging.app src/modules/cn.tuyucheng.taketoday.logging.app/module-info.java src/modules/cn.tuyucheng.taketoday.logging.app/cn/tuyucheng/taketoday/logging/app/*.java

# 运行logging app
java --module-path mods -m cn.tuyucheng.taketoday.logging.app/cn.tuyucheng.taketoday.logging.app.MainApp

# 使用logback运行logging app
java --module-path mods -Dlogback.configurationFile=mods/logback.xml -m cn.tuyucheng.taketoday.logging.app/cn.tuyucheng.taketoday.logging.app.MainApp
