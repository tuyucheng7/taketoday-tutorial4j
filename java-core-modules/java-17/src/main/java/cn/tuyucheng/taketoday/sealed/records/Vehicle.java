package cn.tuyucheng.taketoday.sealed.records;

public sealed interface Vehicle permits Car, Truck {

	String getRegistrationNumber();
}