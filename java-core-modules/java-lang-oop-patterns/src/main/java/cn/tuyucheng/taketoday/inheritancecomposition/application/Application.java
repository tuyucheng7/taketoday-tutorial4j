package cn.tuyucheng.taketoday.inheritancecomposition.application;

import cn.tuyucheng.taketoday.inheritancecomposition.model.Actress;
import cn.tuyucheng.taketoday.inheritancecomposition.model.Computer;
import cn.tuyucheng.taketoday.inheritancecomposition.model.Person;
import cn.tuyucheng.taketoday.inheritancecomposition.model.StandardMemory;
import cn.tuyucheng.taketoday.inheritancecomposition.model.StandardProcessor;
import cn.tuyucheng.taketoday.inheritancecomposition.model.StandardSoundCard;
import cn.tuyucheng.taketoday.inheritancecomposition.model.Waitress;

public class Application {

	public static void main(String[] args) {

		Person person = new Person("John", "john@domain.com", 35);
		Waitress waitress = new Waitress("Mary", "mary@domain.com", 22);
		System.out.println(waitress.serveStarter("mixed salad"));
		System.out.println(waitress.serveMainCourse("steak"));
		System.out.println(waitress.serveDessert("cup of cofee"));
		Actress actress = new Actress("Susan", "susan@domain.com", 30);
		System.out.println(actress.readScript("Psycho"));
		System.out.println(actress.performRole());
		Computer computer = new Computer(new StandardProcessor("Intel I3"), new StandardMemory("Kingston", "1TB"));
		computer.setSoundCard(new StandardSoundCard("Generic Sound Card"));
		System.out.println(computer);
	}
}
