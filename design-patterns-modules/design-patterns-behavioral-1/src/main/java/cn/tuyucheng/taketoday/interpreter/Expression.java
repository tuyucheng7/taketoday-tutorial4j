package cn.tuyucheng.taketoday.interpreter;

import java.util.List;

interface Expression {
	List<String> interpret(Context ctx);
}