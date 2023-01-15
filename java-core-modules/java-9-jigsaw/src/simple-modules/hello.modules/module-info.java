import cn.tuyucheng.taketoday.modules.hello.HelloInterface;
import cn.tuyucheng.taketoday.modules.hello.HelloModules;

module hello.modules {
	exports cn.tuyucheng.taketoday.modules.hello;
	provides HelloInterface with HelloModules;
}