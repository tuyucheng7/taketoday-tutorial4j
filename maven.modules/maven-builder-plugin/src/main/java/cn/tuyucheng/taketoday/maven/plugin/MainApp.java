package cn.tuyucheng.taketoday.maven.plugin;

import cn.tuyucheng.taketoday.database.DataConnection;

public class MainApp {

	public static void main(String args[]) {
		System.out.println(DataConnection.temp());
	}
}