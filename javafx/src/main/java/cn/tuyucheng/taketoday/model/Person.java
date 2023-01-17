package cn.tuyucheng.taketoday.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Person {

	private SimpleIntegerProperty id;
	private SimpleStringProperty name;
	private SimpleBooleanProperty isEmployed;

	public Person(Integer id, String name, boolean isEmployed) {
		this.id = new SimpleIntegerProperty(id);
		this.name = new SimpleStringProperty(name);
		this.isEmployed = new SimpleBooleanProperty(isEmployed);
	}

	public int getId() {
		return id.get();
	}

	public IntegerProperty idProperty() {
		return id;
	}

	public void setId(int id) {
		this.id.set(id);
	}

	public String getName() {
		return name.get();
	}

	public StringProperty nameProperty() {
		return name;
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public boolean getIsEmployed() {
		return isEmployed.get();
	}

	public BooleanProperty isEmployedProperty() {
		return isEmployed;
	}

	public void setIsEmployed(boolean isEmployed) {
		this.isEmployed.set(isEmployed);
	}

	@Override
	public String toString() {
		return "Person{" +
			"id=" + id +
			", name='" + name + '\'' +
			", isEmployed=" + isEmployed +
			'}';
	}
}