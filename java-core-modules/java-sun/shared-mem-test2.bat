@rem This bat file starts the consumer and producer (more or less) at the same time
del target\sharedmem.bin
echo "" > target\sharedmem.bin
cd target\classes
rem start java --add-opens java.base/java.nio=ALL-UNNAMED cn.tuyucheng.taketoday.sharedmem.ProducerAppWithSpinLock ..\sharedmem.bin 65536
rem start java --add-opens java.base/java.nio=ALL-UNNAMED cn.tuyucheng.taketoday.sharedmem.ConsumerAppWithSpinLock ..\sharedmem.bin 65536
start %JAVA_HOME%\bin\java cn.tuyucheng.taketoday.sharedmem.ProducerAppWithSpinLock ..\sharedmem.bin 65536
start %JAVA_HOME%\bin\java cn.tuyucheng.taketoday.sharedmem.ConsumerAppWithSpinLock ..\sharedmem.bin 65536
cd ..
cd ..
