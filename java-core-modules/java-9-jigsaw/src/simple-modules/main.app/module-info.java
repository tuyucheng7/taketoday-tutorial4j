import cn.tuyucheng.taketoday.modules.hello.HelloInterface;

module main.app {
	requires hello.modules;
	uses HelloInterface;
}