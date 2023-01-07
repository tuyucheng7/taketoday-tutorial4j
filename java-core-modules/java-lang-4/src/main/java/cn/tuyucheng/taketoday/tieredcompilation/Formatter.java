package cn.tuyucheng.taketoday.tieredcompilation;

public interface Formatter {

	<T> String format(T object) throws Exception;

}
