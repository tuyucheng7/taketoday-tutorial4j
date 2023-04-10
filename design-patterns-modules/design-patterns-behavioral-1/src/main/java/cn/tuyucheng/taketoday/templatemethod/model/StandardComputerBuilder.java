package cn.tuyucheng.taketoday.templatemethod.model;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StandardComputerBuilder extends ComputerBuilder {

	@Override
	public void addMotherboard() {
		computerParts.put("Motherboard", "Standard Motherboard");
	}

	@Override
	public void setupMotherboard() {
		motherboardSetupStatus.add("Screwing the standard motherboard to the case.");
		motherboardSetupStatus.add("Pluging in the power supply connectors.");
		motherboardSetupStatus.forEach(step -> LOGGER.debug(step));
	}

	@Override
	public void addProcessor() {
		computerParts.put("Processor", "Standard Processor");
	}
}