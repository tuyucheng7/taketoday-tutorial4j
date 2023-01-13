package cn.tuyucheng.taketoday.mustache;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;

public class MustacheUtil {

	private static final MustacheFactory mf = new DefaultMustacheFactory();

	public static MustacheFactory getMustacheFactory() {
		return mf;
	}
}