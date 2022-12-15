package cn.tuyucheng.taketoday.templatemethod.application;

import cn.tuyucheng.taketoday.templatemethod.model.Computer;
import cn.tuyucheng.taketoday.templatemethod.model.ComputerBuilder;
import cn.tuyucheng.taketoday.templatemethod.model.HighEndComputerBuilder;
import cn.tuyucheng.taketoday.templatemethod.model.StandardComputerBuilder;

public class Application {

	public static void main(String[] args) {
		ComputerBuilder standardComputerBuilder = new StandardComputerBuilder();
		Computer standardComputer = standardComputerBuilder.buildComputer();
		standardComputer.getComputerParts().forEach((k, v) -> System.out.println("Part : " + k + " Value : " + v));

		ComputerBuilder highEndComputerBuilder = new HighEndComputerBuilder();
		Computer highEndComputer = highEndComputerBuilder.buildComputer();
		highEndComputer.getComputerParts().forEach((k, v) -> System.out.println("Part : " + k + " Value : " + v));
	}
}