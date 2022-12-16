package cn.tuyucheng.taketoday.proxy;

import cn.tuyucheng.taketoday.jacoco.exclude.annotations.ExcludeFromJacocoGeneratedReport;

@ExcludeFromJacocoGeneratedReport
public class ProxyPatternDriver {
	public static void main(String[] args) {
		ExpensiveObject object = new ExpensiveObjectProxy();
		object.process();
		object.process();
	}
}