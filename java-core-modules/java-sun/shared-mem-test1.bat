@rem This bat file starts the consumer and producer (more or less) at the same time
cd target\classes
rem start java --add-opens java.base/java.nio=ALL-UNNAMED cn.tuyucheng.taketoday.sharedmem.ProducerApp c:\lixo\sharedmem.bin 65536
rem start java --add-opens java.base/java.nio=ALL-UNNAMED cn.tuyucheng.taketoday.sharedmem.ConsumerApp c:\lixo\sharedmem.bin 65536
start %JAVA_HOME%\bin\java cn.tuyucheng.taketoday.sharedmem.ProducerApp c:\lixo\sharedmem.bin 65536
start %JAVA_HOME%\bin\java cn.tuyucheng.taketoday.sharedmem.ConsumerApp c:\lixo\sharedmem.bin 65536
cd ..
cd ..
