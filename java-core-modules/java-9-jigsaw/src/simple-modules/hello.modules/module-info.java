module hello.modules {
   exports cn.tuyucheng.taketoday.modules.hello;
   provides cn.tuyucheng.taketoday.modules.hello.HelloInterface with cn.tuyucheng.taketoday.modules.hello.HelloModules;
}